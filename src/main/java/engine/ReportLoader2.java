package engine;

import annotations.CellValidator;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static exceptions.ReportEngineRuntimeExceptionCode.ILLEGAL_ACCESS;
import static exceptions.ReportEngineRuntimeExceptionCode.ILLEGAL_ARGUMENT;
import static exceptions.ReportEngineRuntimeExceptionCode.INVOCATION_ERROR;

public class ReportLoader2 {

    public enum ReportLoaderErrorTreatment {
        SKIP_ROW_ON_ERROR, SKIP_COLUMN_ON_ERROR, THROW_ERROR
    }

    private String reportName;
    private Workbook currentWorkbook;
    private ReportLoaderResult loaderResult;
    private Translator translator;

    public ReportLoader2(String reportName, String filePath) throws IOException, InvalidFormatException {
        this(reportName, new File(filePath));
    }

    public ReportLoader2(String reportName, File file) throws IOException, InvalidFormatException {
        this(reportName, new FileInputStream(file));
    }

    public ReportLoader2(String reportName, InputStream inputStream) throws IOException, InvalidFormatException {
        this(reportName, WorkbookFactory.create(inputStream));
    }

    private ReportLoader2(String reportName, Workbook workbook) {
        this.reportName = reportName;
        this.currentWorkbook = workbook;
        this.loaderResult = new ReportLoaderResult();
    }

    public <T> ReportLoader2 bindForClass(Class<T> clazz) throws ReportEngineReflectionException, IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        return bindForClass(clazz, ReportLoaderErrorTreatment.THROW_ERROR);
    }

    public <T> ReportLoader2 bindForClass(Class<T> clazz, ReportLoaderErrorTreatment treatment) throws ReportEngineReflectionException, IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final Configuration configuration = AnnotationUtils.getClassReportConfiguration(clazz, reportName);
        this.translator = new Translator(new TranslationsParser(configuration.translationsDir()).parse(configuration.reportLang()));
        final List<ReportColumn> annotationColumns = AnnotationUtils.loadAnnotations(clazz, reportName, false);
        final List<ReportColumn> unwindedAnnotationColumns = AnnotationUtils.loadAnnotations(clazz, reportName, true);
        final List<ReportBlock> reportBlocks = new ArrayList<>();
        getBlocks(reportBlocks, annotationColumns, unwindedAnnotationColumns, null, false, 0);
        doBind(clazz, configuration, reportBlocks);
        return this;
    }

    private <T> List<T> doBind(final Class<T> clazz, final Configuration configuration, final List<ReportBlock> reportBlocks) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        List<T> list = new ArrayList<>();
        final Sheet sheet = currentWorkbook.getSheet(configuration.sheetName());
        Map<Field, List<?>> subreportFieldMap = new HashMap<>();
        for (final ReportBlock reportBlock : reportBlocks) {
            final Class<?> blockClass = reportBlock.getBlockClass();
            if(blockClass.equals(clazz)){
                for(int dataRowNum = configuration.dataStartRowIndex(); dataRowNum <= sheet.getLastRowNum() - AnnotationUtils.getLastSpecialRowsCount(configuration); dataRowNum++) {
                    final Row row = sheet.getRow(dataRowNum);
                    final T instance = ReflectionUtils.newInstance(clazz);
                    for (int columnIndex = 0, i = reportBlock.getStartColumn(); i <= reportBlock.getEndColumn(); i++, columnIndex++) {
                        final Cell cell = row.getCell(i);
                        final ReportColumn reportColumn = reportBlock.getColumn(columnIndex);
                        if (!reportColumn.isSpecialColumn()) {
                            instanceSetValueFromCell(reportColumn.getMethod(), instance, cell);
                        }
                    }
                    list.add(instance);
                }
            } else if(!reportBlock.isRepeatable()){
                List<?> subreportsInstances = doBind(blockClass, configuration, Collections.singletonList(reportBlock));
                subreportFieldMap.put(reportBlock.getParentField(), subreportsInstances);
            }
        }
        collectionSetSubreportValues(list, subreportFieldMap);
        return list;
    }

    private <T> void collectionSetSubreportValues(final List<T> list, Map<Field, List<?>> subreportFieldMap) throws InvocationTargetException, IllegalAccessException {
        for (final Map.Entry<Field, List<?>> entry : subreportFieldMap.entrySet()) {
            final Field field = entry.getKey();
            final List<?> subreports = entry.getValue();
            for (int i = 0; i < list.size(); i++) {
                final T instance = list.get(i);
                final Object subreport = subreports.get(i);
                final Method method = ReflectionUtils.fetchFieldSetter(field, instance.getClass());
                method.setAccessible(true);
                method.invoke(instance, subreport);
            }
        }
    }

    private int getBlocks(List<ReportBlock> reportBlocks, List<ReportColumn> columns, List<ReportColumn> unwindedColumns, Field parentField, boolean isRepeatable, int index){
        ReportBlock reportBlock = null;
        for(int i = 0; i < columns.size() && index < unwindedColumns.size();){
            if(reportBlock == null) {
                reportBlock = new ReportBlock(index, parentField);
                reportBlocks.add(reportBlock);
            }
            final ReportColumn reportColumn = columns.get(i);
            if(reportColumn.getAnnotation().equals(unwindedColumns.get(index).getAnnotation()) &&
                    reportColumn.getParentClass().equals(unwindedColumns.get(index).getParentClass())) {
                reportBlock
                        .setBlockClass(reportColumn.getParentClass())
                        .setEndColumn(index)
                        .addReportColumn(reportColumn)
                        .setRepeatable(isRepeatable);
                index++;
            } else {
                Class<?> clazz;
                if(reportColumn.getFieldClass().equals(List.class)){
                    final Field field = reportColumn.getField();
                    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                    clazz = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                } else {
                    clazz = reportColumn.getFieldClass();
                }
                index = getBlocks(
                        reportBlocks,
                        AnnotationUtils.loadAnnotations(clazz, reportColumn.getReportName(), true),
                        unwindedColumns,
                        reportColumn.getField(),
                        reportColumn.getFieldClass().equals(List.class),
                        i
                );
                reportBlock = null;
            }
            i++;
        }
        return index;
    }

    private void instanceSetValueFromCell(final Method method, final Object instance, final Cell cell/*, final CellValidator[] cellValidators*/) throws ReportEngineReflectionException, ReportEngineValidationException {
        method.setAccessible(true);
        Class<?> parameterType = method.getParameterTypes()[0];
        Object value = null;
        try {
            if(cell != null){
                if(CellType.BOOLEAN.equals(cell.getCellTypeEnum())){
                    value = cell.getBooleanCellValue();
                } else if(CellType.STRING.equals(cell.getCellTypeEnum())){
                    value = cell.getRichStringCellValue().getString();
                } else if(CellType.NUMERIC.equals(cell.getCellTypeEnum())){
                    if (DateUtil.isCellDateFormatted(cell)) {
                        value = cell.getDateCellValue();
                    } else if(parameterType.equals(Double.class) || parameterType.getName().equals("double")){
                        value = cell.getNumericCellValue();
                    } else if(parameterType.equals(Integer.class) || parameterType.getName().equals("int")) {
                        value = new Double(cell.getNumericCellValue()).intValue();
                    } else if(parameterType.equals(Long.class) || parameterType.getName().equals("long")){
                        value = new Double(cell.getNumericCellValue()).longValue();
                    } else if(parameterType.equals(Float.class) || parameterType.getName().equals("float")){
                        value = new Double(cell.getNumericCellValue()).floatValue();
                    } else if(parameterType.equals(Short.class) || parameterType.getName().equals("short")){
                        value = new Double(cell.getNumericCellValue()).shortValue();
                    }
                } else if(CellType.FORMULA.equals(cell.getCellTypeEnum())) {
                    value = cell.getCellFormula();
                }
                //checkValidations(value, cellValidators);
                method.invoke(instance, value);
            }
        } catch (IllegalAccessException e) {
            throw new ReportEngineReflectionException("Error executing method witch does not have access to the definition of the specified class", ILLEGAL_ACCESS);
        } catch (InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error executing method witch does not have access to the definition of the specified class", INVOCATION_ERROR);
        }
    }

    public ReportLoaderResult getLoaderResult() {
        return loaderResult;
    }
}
