package org.greports.engine;

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
import org.greports.utils.ReflectionUtils;

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
    private final XSSFWorkbook currentWorkbook;
    private final ReportLoaderResult loaderResult;
    private ReportLoaderValidator validator;
    private final ReportDataReader reader;

    public ReportLoader(String filePath) throws IOException, InvalidFormatException {
        this(new File(filePath), null);
    }

    public ReportLoader(String filePath, String reportName) throws IOException, InvalidFormatException {
        this(new File(filePath), reportName);
    }

    public ReportLoader(File file) throws IOException, InvalidFormatException {
        this(new FileInputStream(file), null);
    }

    public ReportLoader(File file, String reportName) throws IOException, InvalidFormatException {
        this(new FileInputStream(file), reportName);
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
        return bindForClass(clazz, ReportLoaderErrorTreatment.THROW_ERROR);
    }

    public <T> ReportLoader bindForClass(Class<T> clazz, ReportLoaderErrorTreatment treatment) throws ReportEngineReflectionException {
        if(reportName == null) {
            throw new ReportEngineRuntimeException("reportName cannot be null", this.getClass());
        }
        ReportConfiguration configuration = ReportConfigurationLoader.load(clazz, reportName);
        this.validator = new ReportLoaderValidator(configuration);
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
                        validator.checkColumnValidations(block.getValues(), block.getColumnValidators());
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
            validator.checkCellValidations(value, cellValidators);
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

    public ReportDataReader getReader() {
        return reader;
    }

    public ReportLoaderResult getLoaderResult() {
        return loaderResult;
    }
}
