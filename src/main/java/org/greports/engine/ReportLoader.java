package org.greports.engine;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.greports.annotations.CellValidator;
import org.greports.annotations.ColumnValidator;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.exceptions.ReportEngineValidationException;
import org.greports.positioning.TranslationsParser;
import org.greports.utils.AnnotationUtils;
import org.greports.utils.ConverterUtils;
import org.greports.utils.ReflectionUtils;
import org.greports.utils.Translator;
import org.greports.validators.AbstractCellValidator;
import org.greports.validators.AbstractColumnValidator;
import org.greports.validators.AbstractValidator;
import org.greports.validators.ValidatorFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public <T> ReportLoader bindForClass(Class<T> clazz) throws ReportEngineReflectionException {
        return bindForClass(clazz, ReportLoaderErrorTreatment.THROW_ERROR);
    }

    public <T> ReportLoader bindForClass(Class<T> clazz, ReportLoaderErrorTreatment treatment) throws ReportEngineReflectionException {
        ReportConfiguration configuration = ReportConfigurationLoader.load(clazz, reportName);
        final Map<String, Object> translations = new TranslationsParser(configuration).getTranslations();
        translator = new Translator(translations);
        final ReportBlock reportBlock = new ReportBlock(clazz, reportName, null);
        loadBlocks(reportBlock);
        reportBlock
                .orderBlocks()
                .setBlockIndexes(0);
        final List<T> list = bindBlocks(reportBlock, clazz, configuration, treatment, new ArrayList<>());
        this.loaderResult.addResult(clazz, list);
        return this;
    }


    public void loadBlocks(ReportBlock reportBlock) throws ReportEngineReflectionException {
        final Map<Annotation, Method> annotationMethodMap = AnnotationUtils.loadBlockAnnotations(reportBlock);
        for (final Map.Entry<Annotation, Method> entry : annotationMethodMap.entrySet()) {
            final Annotation annotation = entry.getKey();
            final Method method = entry.getValue();
            final Class<?> blockClass = Optional.ofNullable(method).map(m -> m.getParameterTypes()[0]).orElse(null);
            final ReportBlock block = new ReportBlock(
                blockClass,
                reportBlock.getReportName(),
                reportBlock,
                annotation,
                method,
                ReflectionUtils.isListOrArray(blockClass)
            );
            reportBlock.addBlock(block);
            if (block.isSubreport()) {
                loadBlocks(block);
            }
        }
    }

    public <T> List<T> bindBlocks(ReportBlock reportBlock, Class<T> clazz, ReportConfiguration configuration, ReportLoaderErrorTreatment treatment, List<Integer> skipRows) throws ReportEngineReflectionException {
        List<T> list = new ArrayList<>();
        final Sheet sheet = currentWorkbook.getSheet(configuration.getSheetName());
        boolean errorThrown = false;
        try {
            Method method;
            for (int dataRowNum = configuration.getDataStartRowIndex(); dataRowNum <= sheet.getLastRowNum() - AnnotationUtils.getLastSpecialRowsCount(configuration); dataRowNum++) {
                if (!skipRows.contains(dataRowNum)) {
                    final T instance = ReflectionUtils.newInstance(clazz);
                    final Row row = sheet.getRow(dataRowNum);
                    for (final ReportBlock block : reportBlock.getBlocks()) {
                        if (block.isColumn()) {
                            method = block.getParentMethod();
                            final Cell cell = row.getCell(block.getStartColumn());
                            Object value = null;
                            try {
                                value = getCellValue(method, cell);
                                value = ConverterUtils.convertValue(value, block.getSetterConverters(), block.getBlockClass());
                                instanceSetValue(method, instance, value, block.getCellValidators());
                                block.addValue(value);
                            } catch (ReportEngineValidationException e) {
                                if (ReportLoaderErrorTreatment.THROW_ERROR.equals(treatment)) {
                                    throw e;
                                } else {
                                    loaderResult.addError(clazz, cell, block.getAsColumn().title(), e.getMessage(), (Serializable) value);
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
                    method = block.getParentMethod();
                    for (int i = 0; i < list.size(); i++) {
                        method.invoke(list.get(i), objects.get(i));
                    }
                } else if (block.isColumn()) {
                    try {
                        checkColumnValidations(block.getValues(), block.getColumnValidators());
                    } catch (ReportEngineValidationException e) {
                        loaderResult.addError(clazz, sheet.getSheetName(), e.getRowIndex() + configuration.getDataStartRowIndex(), block.getStartColumn(), block.getAsColumn().title(), e.getMessage(), (Serializable) e.getErrorValue());
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            throw new ReportEngineReflectionException("Error obtaining constructor reference" , e, clazz);
        } catch (InstantiationException e) {
            throw new ReportEngineReflectionException("Error instantiating an object", e, clazz);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error executing method witch does not have access to the definition of the specified class", e, clazz);
        }
        return list;
    }



    private void instanceSetValue(final Method method, final Object instance, final Object value, final List<CellValidator> cellValidators) throws ReportEngineReflectionException, ReportEngineValidationException {
        try {
            method.setAccessible(true);
            checkCellValidations(value, cellValidators);
            method.invoke(instance, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error executing method witch does not have access to the definition of the specified class", e, method.getDeclaringClass());
        }
    }

    private Object getCellValue(final Method method, final Cell cell) {
        Class<?> parameterType = method.getParameterTypes()[0];
        if (cell != null) {
            if(parameterType.equals(Boolean.class) || parameterType.getName().equals("boolean")) {
                return cell.getBooleanCellValue();
            } else if (CellType.FORMULA.equals(cell.getCellTypeEnum())) {
                return cell.getCellFormula();
            } else if(parameterType.equals(String.class) && CellType.STRING.equals(cell.getCellTypeEnum())) {
                return cell.getRichStringCellValue().getString();
            } else if(parameterType.equals(String.class) && CellType.NUMERIC.equals(cell.getCellTypeEnum())) {
                return Double.toString(cell.getNumericCellValue());
            } else if(parameterType.equals(Date.class)) {
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
        }
        return null;
    }

    private void checkColumnValidations(final List<Object> values, final List<ColumnValidator> columnValidators) {
        for (final ColumnValidator columnValidator : columnValidators) {
            try {
                AbstractValidator validatorInstance = ValidatorFactory.get(columnValidator.validatorClass(), columnValidator.param());
                validateColumn((AbstractColumnValidator) validatorInstance, values, columnValidator.errorMessage());
            } catch (ReflectiveOperationException e) {
                throw new ReportEngineValidationException("Error instantiating a validator @" + columnValidator.validatorClass().getSimpleName(), columnValidator.validatorClass());
            }
        }
    }

    private void checkCellValidations(final Object value, final List<CellValidator> cellValidators) throws ReportEngineReflectionException {
        for (final CellValidator cellValidator : cellValidators) {
            try {
                AbstractValidator validatorInstance = ValidatorFactory.get(cellValidator.validatorClass(), cellValidator.value());
                validateCell((AbstractCellValidator) validatorInstance, value, cellValidator.errorMessage());
            } catch (ReflectiveOperationException e) {
                throw new ReportEngineReflectionException("Error instantiating a validator @" + cellValidator.validatorClass().getSimpleName(), e, cellValidator.validatorClass());
            }
        }
    }

    private void validateColumn(final AbstractColumnValidator validatorInstance, final List<Object> values, final String errorMessageKey) {
        if (!validatorInstance.isValid(values)) {
            String errorMessage = translator.translate(errorMessageKey, validatorInstance.getValidatorValue());
            final Integer errorRowIndex = validatorInstance.getErrorRowIndex(values);
            throw new ReportEngineValidationException(errorMessage, validatorInstance.getClass(), errorRowIndex, (Serializable) validatorInstance.getErrorValue());
        }
    }

    private void validateCell(final AbstractCellValidator validatorInstance, final Object value, final String errorMessageKey) throws ReportEngineValidationException {
        if (!validatorInstance.isValid(value)) {
            String errorMessage = translator.translate(errorMessageKey, validatorInstance.getValidatorValue());
            throw new ReportEngineValidationException(errorMessage, validatorInstance.getClass());
        }
    }

    public ReportLoaderResult getLoaderResult() {
        return loaderResult;
    }
}
