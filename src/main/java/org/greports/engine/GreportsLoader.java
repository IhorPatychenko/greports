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
import org.greports.exceptions.GreportsReflectionException;
import org.greports.exceptions.GreportsRuntimeException;
import org.greports.exceptions.GreportsValidationException;
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

public class GreportsLoader {

    public enum ReportLoaderErrorTreatment {
        SKIP_ROW_ON_ERROR, SKIP_COLUMN_ON_ERROR, THROW_ERROR
    }

    private final String reportName;
    private final XSSFWorkbook currentWorkbook;
    private final LoaderResult loaderResult;
    private LoaderValidator validator;
    private final DataReader reader;
    private Translator tranlator;

    public GreportsLoader(String filePath) throws IOException, InvalidFormatException {
        this(new File(filePath), null);
    }

    public GreportsLoader(String filePath, String reportName) throws IOException, InvalidFormatException {
        this(new File(filePath), reportName);
    }

    public GreportsLoader(File file) throws IOException, InvalidFormatException {
        this(file, null);
    }

    public GreportsLoader(File file, String reportName) throws IOException, InvalidFormatException {
        this((XSSFWorkbook) WorkbookFactory.create(file), reportName);
    }

    public GreportsLoader(InputStream inputStream) throws IOException, InvalidFormatException {
        this((XSSFWorkbook) WorkbookFactory.create(inputStream), null);
    }

    public GreportsLoader(InputStream inputStream, String reportName) throws IOException, InvalidFormatException {
        this((XSSFWorkbook) WorkbookFactory.create(inputStream), reportName);
    }

    private GreportsLoader(XSSFWorkbook workbook, String reportName) {
        this.reportName = reportName;
        this.currentWorkbook = workbook;
        this.loaderResult = new LoaderResult();
        this.reader = new DataReader(this.currentWorkbook);
    }

    public <T> GreportsLoader bindForClass(Class<T> clazz) throws GreportsReflectionException {
        return bindForClass(clazz, this.reportName, ReportLoaderErrorTreatment.THROW_ERROR, -1, Integer.MAX_VALUE);
    }

    public <T> GreportsLoader bindForClass(Class<T> clazz, String reportName) throws GreportsReflectionException {
        return bindForClass(clazz, reportName, ReportLoaderErrorTreatment.THROW_ERROR, -1, Integer.MAX_VALUE);
    }

    public <T> GreportsLoader bindForClass(Class<T> clazz, ReportLoaderErrorTreatment errorTreatment) throws GreportsReflectionException {
        return bindForClass(clazz, this.reportName, errorTreatment, -1, Integer.MAX_VALUE);
    }

    public <T> GreportsLoader bindForClass(Class<T> clazz, String reportName, ReportLoaderErrorTreatment errorTreatment) throws GreportsReflectionException {
        return bindForClass(clazz, reportName, errorTreatment, -1, Integer.MAX_VALUE);
    }

    public <T> GreportsLoader bindForClass(Class<T> clazz, String reportName, ReportLoaderErrorTreatment errorTreatment, int fromRow, int toRow) throws GreportsReflectionException {
        if(reportName == null) {
            throw new GreportsRuntimeException("reportName cannot be null", this.getClass());
        }
        Configuration configuration = Configuration.load(clazz, reportName);
        this.tranlator = new Translator(configuration);
        this.validator = new LoaderValidator(configuration);
        final DataBlock dataBlock = new DataBlock(clazz, reportName, null);
        loadBlocks(dataBlock);
        dataBlock
                .orderBlocks()
                .setBlockIndexes(0);
        final List<T> list = bindBlocks(dataBlock, clazz, configuration, errorTreatment, new ArrayList<>(), fromRow, toRow);
        this.loaderResult.addResult(clazz, list);
        return this;
    }

    public void loadBlocks(DataBlock dataBlock) throws GreportsReflectionException {
        final Map<Annotation, Method> annotationMethodMap = AnnotationUtils.loadBlockAnnotations(dataBlock);
        for (final Map.Entry<Annotation, Method> entry : annotationMethodMap.entrySet()) {
            final Annotation annotation = entry.getKey();
            final Method method = entry.getValue();
            final Class<?> blockClass = Optional.ofNullable(method).map(m -> m.getParameterTypes()[0]).orElse(null);
            final DataBlock block = new DataBlock(
                blockClass,
                dataBlock.getReportName(),
                dataBlock,
                annotation,
                method,
                ReflectionUtils.isListOrArray(blockClass)
            );
            dataBlock.addBlock(block);
            if (block.isSubreport()) {
                loadBlocks(block);
            }
        }
    }

    protected <T> List<T> bindBlocks(DataBlock dataBlock, Class<T> clazz, Configuration configuration, ReportLoaderErrorTreatment errorTreatment, List<Integer> skipRows, int fromRow, int toRow) throws GreportsReflectionException {
        List<T> instancesList = new ArrayList<>();
        final Sheet sheet = currentWorkbook.getSheet(configuration.getSheetName());
        boolean errorThrown = false;

        if(fromRow > toRow) {
            throw new GreportsRuntimeException("fromRow cannot be greater than toRow", this.getClass());
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
                    for (final DataBlock block : dataBlock.getBlocks()) {
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

            bindSubBlocks(dataBlock, clazz, configuration, errorTreatment, skipRows, fromRow, toRow, instancesList, sheet);

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new GreportsReflectionException("Error executing method witch does not have access to the definition of the specified class", e, clazz);
        }
        return instancesList;
    }

    private <T> void bindSubBlocks(DataBlock dataBlock, Class<T> clazz, Configuration configuration, ReportLoaderErrorTreatment errorTreatment, List<Integer> skipRows, int fromRow, int toRow, List<T> instancesList, Sheet sheet) throws GreportsReflectionException, IllegalAccessException, InvocationTargetException {
        Method method;
        for (final DataBlock block : dataBlock.getBlocks()) {
            if (block.isSubreport()) {
                final List<?> objects = bindBlocks(block, block.getBlockClass(), configuration, errorTreatment, skipRows, fromRow, toRow);
                method = block.getParentMethod();
                for (int i = 0; i < instancesList.size(); i++) {
                    method.invoke(instancesList.get(i), objects.get(i));
                }
            } else if (block.isColumn()) {
                try {
                    validator.checkColumnValidations(block.getValues(), block.getColumnValidators());
                } catch (GreportsValidationException e) {
                    loaderResult.addError(clazz, sheet.getSheetName(), e.getRowIndex() + configuration.getDataStartRowIndex(), block.getStartColumn(), block.getAsColumn().title(), tranlator.translate(e.getMessage()), (Serializable) e.getErrorValue());
                }
            }
        }
    }

    private <T> boolean bindCellValueToClassAttr(Class<T> clazz, Method method, T instance, DataBlock block, Cell cell, ReportLoaderErrorTreatment errorTreatment) throws GreportsReflectionException {
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

    private void instanceSetValue(final Method method, final Object instance, final Object value, final List<CellValidator> cellValidators) throws GreportsReflectionException {
        try {
            validator.checkCellValidations(value, cellValidators);
            method.invoke(instance, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new GreportsReflectionException("Error executing method witch does not have access to the definition of the specified class", e, method.getDeclaringClass());
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

    public DataReader getReader() {
        return reader;
    }

    public LoaderResult getLoaderResult() {
        return loaderResult;
    }

    public void close() throws IOException {
        if(!Objects.isNull(this.currentWorkbook)) {
            this.currentWorkbook.close();
        }
    }
}
