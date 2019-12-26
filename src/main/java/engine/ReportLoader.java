package engine;

import annotations.Column;
import annotations.Report;
import annotations.Configuration;
import annotations.SpecialColumn;
import annotations.Subreport;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import utils.AnnotationUtils;
import utils.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ReportLoader {

    private String reportName;
    private Workbook currentWorkbook;

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
    }

    public <T> List<T> bindForClass(Class<T> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final Configuration configuration = getClassReportConfiguration(clazz);
        final Map<Annotation, Pair<Class, Method>> orderedAnnotations = loadColumns(clazz, configuration);
        final Map<Class, List<Pair<Integer, Method>>> groupColumnsByClass = groupByClass(orderedAnnotations);
        return bindData(clazz, configuration, groupColumnsByClass);
    }

    private <T> List<T> bindData(Class<T> clazz, Configuration configuration, Map<Class, List<Pair<Integer, Method>>> groupColumnsByClass) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        List<T> list = new ArrayList<>();
        final Sheet sheet = currentWorkbook.getSheet(configuration.sheetName());
        for(int i = configuration.dataOffset(); i < sheet.getLastRowNum(); i++) {
            final Row row = sheet.getRow(i);
            List<Object> instances = new ArrayList<>();
            for (Map.Entry<Class, List<Pair<Integer, Method>>> entry : groupColumnsByClass.entrySet()) {
                final Class aClass = entry.getKey();
                final Constructor declaredConstructor = aClass.getDeclaredConstructors()[0];
                declaredConstructor.setAccessible(true);
                final Object instance = declaredConstructor.newInstance();
                final List<Pair<Integer, Method>> pairs = entry.getValue();
                for (Pair<Integer, Method> pair : pairs) {
                    final Integer cellIndex = pair.getLeft();
                    final Method method = pair.getRight();
                    final Cell cell = row.getCell(cellIndex);
                    instanceSetValueFromCell(method, instance, cell);
                }
                instances.add(instance);
            }
            System.out.println(instances);
        }
        return list;
    }

    private Map<Class, List<Pair<Integer, Method>>> groupByClass(Map<Annotation, Pair<Class, Method>> orderedAnnotations) {
        Map<Class, List<Pair<Integer, Method>>> map = new LinkedHashMap<>();
        Integer columnPosition = 0;
        for (Map.Entry<Annotation, Pair<Class, Method>> entry : orderedAnnotations.entrySet()) {
            final Pair<Class, Method> pair = entry.getValue();
            final Class clazz = pair.getLeft();
            final Method method = pair.getRight();
            if(!map.containsKey(clazz)){
                final ArrayList<Pair<Integer, Method>> pairs = new ArrayList<>();
                pairs.add(new Pair<>(columnPosition, method));
                map.put(clazz, pairs);
            } else {
                map.get(clazz).add(new Pair<>(columnPosition, method));
            }
            columnPosition++;
        }
        return map;
    }

    private Configuration getClassReportConfiguration(Class clazz) {
        final Report reportAnnotation = AnnotationUtils.getReportAnnotation(clazz);
        return AnnotationUtils.getReportConfiguration(reportAnnotation, reportName);
    }

    private <T> Map<Annotation, Pair<Class, Method>> loadColumns(Class<T> clazz, Configuration configuration) throws NoSuchMethodException {
        Map<Annotation, Pair<Class, Method>> sortedAnnotations = new LinkedHashMap<>();

        Map<Column, Pair<Class, Method>> columnsMap = new LinkedHashMap<>();
        final Function<Pair<Column, Pair<Class, Method>>, Void> columnsFunction = AnnotationUtils.getColumnsWithFieldAndMethodsFunction(columnsMap);
        AnnotationUtils.columnsWithMethodAnnotations(clazz, columnsFunction, reportName);
        sortedAnnotations.putAll(columnsMap);

        Map<Subreport, Pair<Class, Method>> subreportsMap = new LinkedHashMap<>();
        final Function<Pair<Subreport, Pair<Class, Method>>, Void> subreportsFunction = AnnotationUtils.getSubreportsWithFieldsAndMethodsFunction(subreportsMap);
        AnnotationUtils.subreportsWithFieldsAndMethodAnnotations(clazz, subreportsFunction, reportName);

        final SpecialColumn[] specialColumns = configuration.specialColumns();

        for (Map.Entry<Subreport, Pair<Class, Method>> entry : subreportsMap.entrySet()) {
            final Map<Column, Pair<Class, Method>> map = loadColumns(entry.getValue().getLeft(), getClassReportConfiguration(entry.getValue().getLeft()));
            sortedAnnotations.putAll(map);
        }

        for (SpecialColumn specialColumn : specialColumns) {
            sortedAnnotations.put(specialColumn, new Pair<>(null, null));
        }

        return sortAnnotationsByPosition(sortedAnnotations);
    }

    private static Map<Annotation, Pair<Class, Method>> sortAnnotationsByPosition(Map<Annotation, Pair<Class, Method>> map) {
        List<Map.Entry<Annotation, Pair<Class, Method>>> list = new LinkedList<>(map.entrySet());
        list.sort((o1, o2) -> {
            final Annotation key1 = o1.getKey(), key2 = o2.getKey();
            Float value1, value2;
            if(key1 instanceof Column){
                value1 = ((Column) key1).position();
            } else {
                value1 = ((SpecialColumn) key1).position();
            }
            if(key2 instanceof Column){
                value2 = ((Column) key2).position();
            } else {
                value2 = ((SpecialColumn) key2).position();
            }
            return value1.compareTo(value2);
        });

        Map<Annotation, Pair<Class, Method>> result = new LinkedHashMap<>();
        for (Map.Entry<Annotation, Pair<Class, Method>> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private int getLastColumnIndex(Configuration configuration){
        final Sheet sheet = currentWorkbook.getSheet(configuration.sheetName());
        int lastColumnIndex = 0;
        for(int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum(); i++){
            if(lastColumnIndex < sheet.getRow(i).getLastCellNum()) {
                lastColumnIndex = sheet.getRow(i).getLastCellNum();
            }
        }
        return lastColumnIndex;
    }

//    public <T> List<T> bindForClass(Class<T> clazz) {
//        final Report reportAnnotation = AnnotationUtils.getReportAnnotation(clazz);
//        final Configuration configuration = AnnotationUtils.getReportConfiguration(reportAnnotation, reportName);
//        final List<AbstractMap.SimpleEntry<Method, LoaderColumn>> simpleEntries = reportLoaderMethodsWithColumnAnnotations(clazz);
//        return this.loadData(configuration, simpleEntries, clazz);
//    }

//    private <T> List<T> loadData(Configuration configuration, List<AbstractMap.SimpleEntry<Method, LoaderColumn>> simpleEntries, Class<T> clazz) {
//        List<T> data = new ArrayList<>();
//        Sheet sheet = currentWorkbook.getSheet(configuration.sheetName());
//        for(int i = configuration.dataOffset(); i <= sheet.getLastRowNum(); i++) {
//            try {
//                final T instance = clazz.newInstance();
//                final Row row = sheet.getRow(i);
//                for(int cellIndex = row.getFirstCellNum(), methodIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++, methodIndex++) {
//                    final Cell cell = row.getCell(cellIndex);
//                    final Method method = simpleEntries.get(methodIndex).getKey();
//                    instanceSetValueFromCell(method, instance, cell);
//                }
//                data.add(instance);
//            } catch(InstantiationException e) {
//                throw new RuntimeException("Cannot create new instance of @" + clazz.getSimpleName() + " class. Needs to have a constructor without parameters");
//            } catch (InvocationTargetException | IllegalAccessException ignored) {}
//        }
//        return data;
//    }

    private <T> void instanceSetValueFromCell(final Method method, final Object instance, final Cell cell) throws InvocationTargetException, IllegalAccessException {
        method.setAccessible(true);
        if(CellType.BOOLEAN.equals(cell.getCellTypeEnum())){
            method.invoke(instance, cell.getBooleanCellValue());
        } else if(CellType.STRING.equals(cell.getCellTypeEnum())){
            method.invoke(instance, cell.getRichStringCellValue().getString());
        } else if(CellType.NUMERIC.equals(cell.getCellTypeEnum())){
            if (DateUtil.isCellDateFormatted(cell)) {
                method.invoke(instance, cell.getDateCellValue());
            } else {
                final Class<?> parameterType = method.getParameterTypes()[0];
                if(parameterType.equals(Double.class) || parameterType.getName().equals("double")){
                    method.invoke(instance, cell.getNumericCellValue());
                } else if(parameterType.equals(Integer.class) || parameterType.getName().equals("int")) {
                    method.invoke(instance, new Double(cell.getNumericCellValue()).intValue());
                } else if(parameterType.equals(Long.class) || parameterType.getName().equals("long")){
                    method.invoke(instance, new Double(cell.getNumericCellValue()).longValue());
                } else if(parameterType.equals(Float.class) || parameterType.getName().equals("float")){
                    method.invoke(instance, new Double(cell.getNumericCellValue()).floatValue());
                } else if(parameterType.equals(Short.class) || parameterType.getName().equals("short")){
                    method.invoke(instance, new Double(cell.getNumericCellValue()).shortValue());
                }
            }
        } else if(CellType.FORMULA.equals(cell.getCellTypeEnum())) {
            method.invoke(instance, cell.getCellFormula());
        } else {
            method.invoke(instance, "");
        }
    }

//    private <T> List<AbstractMap.SimpleEntry<Method, LoaderColumn>> reportLoaderMethodsWithColumnAnnotations(Class<T> clazz){
//        List<AbstractMap.SimpleEntry<Method, LoaderColumn>> list = new ArrayList<>();
//        Function<AbstractMap.SimpleEntry<Method, LoaderColumn>, Void> columnFunction = entry -> {
//            list.add(entry);
//            return null;
//        };
//        AnnotationUtils.loaderMethodsWithColumnAnnotations(clazz, columnFunction, reportName);
//        return list;
//    }

}
