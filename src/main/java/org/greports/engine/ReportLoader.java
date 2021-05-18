package org.greports.engine;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greports.annotations.CellValidator;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.exceptions.ReportEngineRuntimeException;
import org.greports.exceptions.ReportEngineValidationException;
import org.greports.utils.AnnotationUtils;
import org.greports.utils.ConverterUtils;
import org.greports.utils.NumberFactory;
import org.greports.utils.ReflectionUtils;
import org.greports.utils.Translator;

import java.io.File;
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
import java.util.Objects;
import java.util.Optional;

public class ReportLoader {

    public enum ReportLoaderErrorTreatment {
        SKIP_ROW_ON_ERROR, SKIP_COLUMN_ON_ERROR, THROW_ERROR
    }

    private final String reportName;
    private final XSSFWorkbook currentWorkbook;
    private final ReportLoaderResult loaderResult;
    private ReportLoaderValidator validator;
    private final ReportDataReader reader;
    private Translator tranlator;

    public ReportLoader(String filePath) throws IOException, InvalidFormatException {
        this(new File(filePath), null);
    }

    public ReportLoader(String filePath, String reportName) throws IOException, InvalidFormatException {
        this(new File(filePath), reportName);
    }

    public ReportLoader(File file) throws IOException, InvalidFormatException {
        this(file, null);
    }

    public ReportLoader(File file, String reportName) throws IOException, InvalidFormatException {
        this((XSSFWorkbook) WorkbookFactory.create(file), reportName);
    }

    public ReportLoader(InputStream inputStream) throws IOException, InvalidFormatException {
        this((XSSFWorkbook) WorkbookFactory.create(inputStream), null);
    }

    public ReportLoader(InputStream inputStream, String reportName) throws IOException, InvalidFormatException {
        this((XSSFWorkbook) WorkbookFactory.create(inputStream), reportName);
    }

    private ReportLoader(XSSFWorkbook workbook, String reportName) {
        this.reportName = reportName;
        this.currentWorkbook = workbook;
        this.loaderResult = new ReportLoaderResult();
        this.reader = new ReportDataReader(this.currentWorkbook);
    }

    public <T> ReportLoader bindForClass(Class<T> clazz) throws ReportEngineReflectionException {
        return bindForClass(clazz, this.reportName, ReportLoaderErrorTreatment.THROW_ERROR, -1, Integer.MAX_VALUE);
    }

    public <T> ReportLoader bindForClass(Class<T> clazz, String reportName) throws ReportEngineReflectionException {
        return bindForClass(clazz, reportName, ReportLoaderErrorTreatment.THROW_ERROR, -1, Integer.MAX_VALUE);
    }

    public <T> ReportLoader bindForClass(Class<T> clazz, ReportLoaderErrorTreatment errorTreatment) throws ReportEngineReflectionException {
        return bindForClass(clazz, this.reportName, errorTreatment, -1, Integer.MAX_VALUE);
    }

    public <T> ReportLoader bindForClass(Class<T> clazz, String reportName, ReportLoaderErrorTreatment errorTreatment) throws ReportEngineReflectionException {
        return bindForClass(clazz, reportName, errorTreatment, -1, Integer.MAX_VALUE);
    }

    public <T> ReportLoader bindForClass(Class<T> clazz, String reportName, ReportLoaderErrorTreatment errorTreatment, int fromRow, int toRow) throws ReportEngineReflectionException {
        if(reportName == null) {
            throw new ReportEngineRuntimeException("reportName cannot be null", this.getClass());
        }
        ReportConfiguration configuration = ReportConfigurationLoader.load(clazz, reportName);
        this.tranlator = new Translator(configuration);
        this.validator = new ReportLoaderValidator(configuration);
        final ReportBlock reportBlock = new ReportBlock(clazz, reportName, null);
        loadBlocks(reportBlock);
        reportBlock
                .orderBlocks()
                .setBlockIndexes(0);
        final List<T> list = bindBlocks(reportBlock, clazz, configuration, errorTreatment, new ArrayList<>(), fromRow, toRow);
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

    protected <T> List<T> bindBlocks(ReportBlock reportBlock, Class<T> clazz, ReportConfiguration configuration, ReportLoaderErrorTreatment errorTreatment, List<Integer> skipRows, int fromRow, int toRow) throws ReportEngineReflectionException {
        List<T> instancesList = new ArrayList<>();
        final Sheet sheet = currentWorkbook.getSheet(configuration.getSheetName());
        boolean errorThrown = false;

        if(fromRow > toRow) {
            throw new ReportEngineRuntimeException("fromRow cannot be greater than toRow", this.getClass());
        }

        try {
            Method method;
            int dataRowNum = configuration.getDataStartRowIndex();
            int dataRowMaxNum = sheet.getLastRowNum() - AnnotationUtils.getLastSpecialRowsCount(configuration);

            if(fromRow > -1) {
                dataRowNum = configuration.getDataStartRowIndex() + fromRow;
            }
            if(Integer.MAX_VALUE != toRow) {
                dataRowMaxNum = configuration.getDataStartRowIndex() + toRow;
            }

            for (; dataRowNum <= dataRowMaxNum; dataRowNum++) {
                if (!skipRows.contains(dataRowNum)) {
                    final T instance = ReflectionUtils.newInstance(clazz);
                    final Row row = sheet.getRow(dataRowNum);
                    for (final ReportBlock block : reportBlock.getBlocks()) {
                        if (block.isColumn()) {
                            method = block.getParentMethod();
                            final Cell cell = row.getCell(block.getStartColumn(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            errorThrown = bindCellValueToClassAttr(clazz, method, instance, block, cell, errorTreatment);
                        }
                    }
                    if (!errorThrown || !ReportLoaderErrorTreatment.SKIP_ROW_ON_ERROR.equals(errorTreatment)) {
                        instancesList.add(instance);
                    }
                    if (errorThrown && ReportLoaderErrorTreatment.SKIP_ROW_ON_ERROR.equals(errorTreatment)) {
                        skipRows.add(dataRowNum);
                    }
                    errorThrown = false;
                }
            }

            bindSubBlocks(reportBlock, clazz, configuration, errorTreatment, skipRows, fromRow, toRow, instancesList, sheet);

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error executing method witch does not have access to the definition of the specified class", e, clazz);
        }
        return instancesList;
    }

    private <T> void bindSubBlocks(ReportBlock reportBlock, Class<T> clazz, ReportConfiguration configuration, ReportLoaderErrorTreatment errorTreatment, List<Integer> skipRows, int fromRow, int toRow, List<T> instancesList, Sheet sheet) throws ReportEngineReflectionException, IllegalAccessException, InvocationTargetException {
        Method method;
        for (final ReportBlock block : reportBlock.getBlocks()) {
            if (block.isSubreport()) {
                final List<?> objects = bindBlocks(block, block.getBlockClass(), configuration, errorTreatment, skipRows, fromRow, toRow);
                method = block.getParentMethod();
                for (int i = 0; i < instancesList.size(); i++) {
                    method.invoke(instancesList.get(i), objects.get(i));
                }
            } else if (block.isColumn()) {
                try {
                    validator.checkColumnValidations(block.getValues(), block.getColumnValidators());
                } catch (ReportEngineValidationException e) {
                    loaderResult.addError(clazz, sheet.getSheetName(), e.getRowIndex() + configuration.getDataStartRowIndex(), block.getStartColumn(), block.getAsColumn().title(), tranlator.translate(e.getMessage()), (Serializable) e.getErrorValue());
                }
            }
        }
    }

    private <T> boolean bindCellValueToClassAttr(Class<T> clazz, Method method, T instance, ReportBlock block, Cell cell, ReportLoaderErrorTreatment errorTreatment) throws ReportEngineReflectionException {
        Object value = null;
        try {
            value = getCellValue(method, cell);
            value = ConverterUtils.convertValue(value, block.getSetterConverter());
            instanceSetValue(method, instance, value, block.getCellValidators());
            block.addValue(value);
        } catch (RuntimeException e) {
            if (ReportLoaderErrorTreatment.THROW_ERROR.equals(errorTreatment)) {
                throw e;
            } else {
                loaderResult.addError(clazz, instance, cell, block.getAsColumn().title(), tranlator.translate(e.getMessage()), (Serializable) value);
                return true;
            }
        }
        return false;
    }

    private void instanceSetValue(final Method method, final Object instance, final Object value, final List<CellValidator> cellValidators) throws ReportEngineReflectionException {
        try {
            validator.checkCellValidations(value, cellValidators);
            method.invoke(instance, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error executing method witch does not have access to the definition of the specified class", e, method.getDeclaringClass());
        }
    }

    private Object getCellValue(final Method method, final Cell cell) {
        Class<?> parameterType = method.getParameterTypes()[0];
        if (cell != null) {
            if(parameterType.equals(Boolean.class) || parameterType.equals(boolean.class)) {
                return cell.getBooleanCellValue();
            } else if (CellType.FORMULA.equals(cell.getCellTypeEnum())) {
                return cell.getCellFormula();
            } else if(parameterType.equals(String.class) && CellType.STRING.equals(cell.getCellTypeEnum())) {
                return cell.getRichStringCellValue().getString();
            } else if(parameterType.equals(String.class) && CellType.NUMERIC.equals(cell.getCellTypeEnum())) {
                return Double.toString(cell.getNumericCellValue());
            } else if(parameterType.equals(Character.class) || parameterType.equals(char.class)) {
                String string = cell.getRichStringCellValue().getString();
                return StringUtils.isEmpty(string) ? null : string.charAt(0);
            } else if(parameterType.equals(Date.class)) {
                return cell.getDateCellValue();
            } else if(Number.class.isAssignableFrom(parameterType) || parameterType.isPrimitive()) {
                return NumberFactory.valueOf(cell.getNumericCellValue(), parameterType);
            }
        }
        return null;
    }

    public ReportDataReader getReader() {
        return reader;
    }

    public ReportLoaderResult getLoaderResult() {
        return loaderResult;
    }

    public void close() throws IOException {
        if(!Objects.isNull(this.currentWorkbook)) {
            this.currentWorkbook.close();
        }
    }
}
