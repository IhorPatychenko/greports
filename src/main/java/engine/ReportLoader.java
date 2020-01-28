package engine;

import annotations.CellValidator;
import annotations.ColumnValidator;
import annotations.Configuration;
import exceptions.ReportEngineReflectionException;
import exceptions.ReportEngineValidationException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import positioning.TranslationsParser;
import utils.AnnotationUtils;
import utils.ReflectionUtils;
import utils.Translator;
import validators.AbstractCellValidator;
import validators.AbstractColumnValidator;
import validators.AbstractValidator;
import validators.ValidatorFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static exceptions.ReportEngineRuntimeExceptionCode.ILLEGAL_ACCESS;
import static exceptions.ReportEngineRuntimeExceptionCode.INSTANTIATION_ERROR;
import static exceptions.ReportEngineRuntimeExceptionCode.INVOCATION_ERROR;
import static exceptions.ReportEngineRuntimeExceptionCode.VALIDATION_ERROR;

public class ReportLoader {

    public enum ReportLoaderErrorTreatment {
        SKIP_ROW_ON_ERROR, SKIP_COLUMN_ON_ERROR, THROW_ERROR
    }

    private final String reportName;
    private final Workbook currentWorkbook;
    private final ReportLoaderResult loaderResult;
    private Translator translator;

    public ReportLoader(String reportName, String filePath) throws IOException, InvalidFormatException {
        this(reportName, new File(filePath));
    }

    public ReportLoader(String reportName, File file) throws IOException, InvalidFormatException {
        this(reportName, new FileInputStream(file));
    }

    public ReportLoader(String reportName, InputStream inputStream) throws IOException, InvalidFormatException {
        this(reportName, WorkbookFactory.create(inputStream));
    }

    private ReportLoader(String reportName, Workbook workbook) {
        this.reportName = reportName;
        this.currentWorkbook = workbook;
        this.loaderResult = new ReportLoaderResult();
    }

    public <T> ReportLoader bindForClass(Class<T> clazz) throws ReportEngineReflectionException, IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        return bindForClass(clazz, ReportLoaderErrorTreatment.THROW_ERROR);
    }

    public <T> ReportLoader bindForClass(Class<T> clazz, ReportLoaderErrorTreatment treatment) throws ReportEngineReflectionException, IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final Configuration configuration = AnnotationUtils.getClassReportConfiguration(clazz, reportName);
        this.translator = new Translator(new TranslationsParser(configuration.translationsDir()).parse(configuration.reportLang()));
        final ReportBlock reportBlock = new ReportBlock(clazz, reportName, null);
        loadBlocks(reportBlock);
        reportBlock
                .orderBlocks()
                .setBlockIndexes(0);
        final List<T> list = bindBlocks(reportBlock, clazz, configuration, treatment, new ArrayList<>());
        this.loaderResult.addResult(clazz, list);
        return this;
    }

    public void loadBlocks(ReportBlock reportBlock) {
        final Map<Annotation, Field> annotationFieldMap = AnnotationUtils.loadBlockAnnotations(reportBlock);
        for (final Map.Entry<Annotation, Field> entry : annotationFieldMap.entrySet()) {
            final Annotation annotation = entry.getKey();
            final Field field = entry.getValue();
            final ReportBlock block = new ReportBlock(Optional.ofNullable(field).map(Field::getType).orElse(null), reportBlock.getReportName(), reportBlock, annotation, field);
            reportBlock.addBlock(block);
            if (block.isSubreport()) {
                loadBlocks(block);
            }
        }
    }

    public <T> List<T> bindBlocks(ReportBlock reportBlock, Class<T> clazz, Configuration configuration, ReportLoaderErrorTreatment treatment, List<Integer> skipRows) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<T> list = new ArrayList<>();
        final Sheet sheet = currentWorkbook.getSheet(configuration.sheetName());
        boolean errorThrown = false;
        for (int dataRowNum = configuration.dataStartRowIndex(); dataRowNum <= sheet.getLastRowNum() - AnnotationUtils.getLastSpecialRowsCount(configuration); dataRowNum++) {
            if (!skipRows.contains(dataRowNum)) {
                final T instance = ReflectionUtils.newInstance(clazz);
                final Row row = sheet.getRow(dataRowNum);
                for (final ReportBlock block : reportBlock.getBlocks()) {
                    if (block.isColumn()) {
                        final Method method = ReflectionUtils.fetchFieldSetter(block.getParentField(), clazz);
                        final Cell cell = row.getCell(block.getStartColumn());
                        try {
                            final Object value = getCellValue(method, cell);
                            instanceSetValue(method, instance, value, block.getCellValidators());
                            block.addValue(value);
                        } catch (ReportEngineValidationException e) {
                            if (ReportLoaderErrorTreatment.THROW_ERROR.equals(treatment)) {
                                throw e;
                            } else {
                                loaderResult.addError(clazz, cell, block.getAsColumn().title(), e.getMessage());
                                errorThrown = true;
                            }
                        }
                    }
                }
                if (!errorThrown || !ReportLoaderErrorTreatment.SKIP_ROW_ON_ERROR.equals(treatment)) {
                    list.add(instance);
                }
                if (errorThrown && ReportLoaderErrorTreatment.SKIP_ROW_ON_ERROR.equals(treatment)) {
                    skipRows.add(dataRowNum);
                }
                errorThrown = false;
            }
        }

        for (final ReportBlock block : reportBlock.getBlocks()) {
            if (block.isSubreport()) {
                final List<?> objects = bindBlocks(block, block.getBlockClass(), configuration, treatment, skipRows);
                final Method method = ReflectionUtils.fetchFieldSetter(block.getParentField(), clazz);
                for (int i = 0; i < list.size(); i++) {
                    method.invoke(list.get(i), objects.get(i));
                }
            } else if (block.isColumn()) {
                try {
                    checkColumnValidations(block.getValues(), block.getColumnValidators());
                } catch (ReportEngineValidationException e) {
                    loaderResult.addError(clazz, sheet.getSheetName(), e.getRowIndex() + configuration.dataStartRowIndex(), block.getStartColumn(), block.getAsColumn().title(), e.getMessage());
                }
            }
        }
        return list;
    }

    private void instanceSetValue(final Method method, final Object instance, final Object value, final List<CellValidator> cellValidators) throws ReportEngineReflectionException, ReportEngineValidationException {
        try {
            method.setAccessible(true);
            checkCellValidations(value, cellValidators);
            method.invoke(instance, value);
        } catch (IllegalAccessException e) {
            throw new ReportEngineReflectionException("Error executing method witch does not have access to the definition of the specified class", ILLEGAL_ACCESS);
        } catch (InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error executing method witch does not have access to the definition of the specified class", INVOCATION_ERROR);
        }
    }

    private Object getCellValue(final Method method, final Cell cell) {
        Class<?> parameterType = method.getParameterTypes()[0];
        if (cell != null) {
            if (CellType.BOOLEAN.equals(cell.getCellTypeEnum())) {
                return cell.getBooleanCellValue();
            } else if (CellType.STRING.equals(cell.getCellTypeEnum())) {
                return cell.getRichStringCellValue().getString();
            } else if (CellType.NUMERIC.equals(cell.getCellTypeEnum())) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else if (parameterType.equals(Double.class) || parameterType.getName().equals("double")) {
                    return cell.getNumericCellValue();
                } else if (parameterType.equals(Integer.class) || parameterType.getName().equals("int")) {
                    return new Double(cell.getNumericCellValue()).intValue();
                } else if (parameterType.equals(Long.class) || parameterType.getName().equals("long")) {
                    return new Double(cell.getNumericCellValue()).longValue();
                } else if (parameterType.equals(Float.class) || parameterType.getName().equals("float")) {
                    return new Double(cell.getNumericCellValue()).floatValue();
                } else if (parameterType.equals(Short.class) || parameterType.getName().equals("short")) {
                    return new Double(cell.getNumericCellValue()).shortValue();
                }
            } else if (CellType.FORMULA.equals(cell.getCellTypeEnum())) {
                return cell.getCellFormula();
            }
        }
        return null;
    }

    private void checkColumnValidations(final List<Object> values, final List<ColumnValidator> columnValidators) {
        for (final ColumnValidator columnValidator : columnValidators) {
            try {
                AbstractValidator validatorInstance = ValidatorFactory.get(columnValidator.validatorClass(), columnValidator.param());
                validateColumn((AbstractColumnValidator) validatorInstance, values, columnValidator.errorMessage());
            } catch (ReflectiveOperationException e) {
                throw new ReportEngineValidationException("Error instantiating a validator @" + columnValidator.validatorClass().getSimpleName(), INSTANTIATION_ERROR);
            }
        }
    }

    private void checkCellValidations(final Object value, final List<CellValidator> cellValidators) throws ReportEngineValidationException, ReportEngineReflectionException {
        for (final CellValidator cellValidator : cellValidators) {
            try {
                AbstractValidator validatorInstance = ValidatorFactory.get(cellValidator.validatorClass(), cellValidator.value());
                validateCell((AbstractCellValidator) validatorInstance, value, cellValidator.errorMessage());
            } catch (ReflectiveOperationException e) {
                throw new ReportEngineValidationException("Error instantiating a validator @" + cellValidator.validatorClass().getSimpleName(), INSTANTIATION_ERROR);
            }
        }
    }

    private void validateColumn(final AbstractColumnValidator validatorInstance, final List<Object> values, final String errorMessageKey) {
        if (!validatorInstance.isValid(values)) {
            String errorMessage = translator.translate(errorMessageKey, validatorInstance.getValidatorValue());
            final Integer errorRowIndex = validatorInstance.getErrorRowIndex(values);
            throw new ReportEngineValidationException(errorMessage.replace("%value%", errorMessage), VALIDATION_ERROR, errorRowIndex);
        }
    }

    private void validateCell(final AbstractCellValidator validatorInstance, final Object value, final String errorMessageKey) throws ReportEngineValidationException {
        if (!validatorInstance.isValid(value)) {
            String errorMessage = translator.translate(errorMessageKey, validatorInstance.getValidatorValue());
            throw new ReportEngineValidationException(errorMessage.replace("%value%", errorMessage), VALIDATION_ERROR);
        }
    }

    public ReportLoaderResult getLoaderResult() {
        return loaderResult;
    }
}
