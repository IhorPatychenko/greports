package engine;

import annotations.Column;
import annotations.Report;
import annotations.Configuration;
import annotations.SpecialColumn;
import annotations.Subreport;
import exceptions.ReportEngineReflectionException;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static exceptions.ReportEngineRuntimeExceptionCode.*;
import static exceptions.ReportEngineRuntimeExceptionCode.INSTANTIATION_ERROR;

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

    public <T> List<T> bindForClass(Class<T> clazz) throws ReportEngineReflectionException {
        final Configuration configuration = getClassReportConfiguration(clazz);
        final Map<Annotation, Pair<Class<?>, Method>> annotations = loadColumns(clazz, configuration, false);
        final Map<Annotation, Pair<Class<?>, Method>> unwindedAnnotations = loadColumns(clazz, configuration, true);
        return bindForClass(clazz, configuration, annotations, unwindedAnnotations);
    }

    private <T> List<T> bindForClass(Class<T> clazz, Configuration configuration, Map<Annotation, Pair<Class<?>, Method>> annotations, Map<Annotation, Pair<Class<?>, Method>> unwindedAnnotations) throws ReportEngineReflectionException {
        List<Pair<List<?>, Method>> subreportsData = new ArrayList<>();
        List<T> instances = new ArrayList<>();
        List<Annotation> keys = new ArrayList<>(unwindedAnnotations.keySet());
        try {
            final Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            final Sheet sheet = currentWorkbook.getSheet(configuration.sheetName());
            for(int  i = configuration.dataOffset(); i <= sheet.getLastRowNum(); i++) {
                final Row row = sheet.getRow(i);
                final T instance = declaredConstructor.newInstance();
                for (final Map.Entry<Annotation, Pair<Class<?>, Method>> entry : annotations.entrySet()) {
                    final Annotation annotation = entry.getKey();
                    final Pair<Class<?>, Method> pair = entry.getValue();
                    if(annotation instanceof Column){
                        final Method method = pair.getRight();
                        instanceSetValueFromCell(method, instance, row.getCell(keys.indexOf(annotation)));
                    } else if(annotation instanceof Subreport){
                        final Class<?> subreportClass = pair.getLeft();
                        final Configuration subreportConfiguration = getClassReportConfiguration(subreportClass);
                        final Map<Annotation, Pair<Class<?>, Method>> subreportAnnotations = loadColumns(subreportClass, subreportConfiguration, false);
                        final List<?> list = bindForClass(subreportClass, subreportConfiguration, subreportAnnotations, unwindedAnnotations);
                        subreportsData.add(new Pair<>(list, pair.getRight()));
                    }
                }
                instances.add(instance);
            }

            for (final Pair<List<?>, Method> pair : subreportsData) {
                final Method method = pair.getRight();
                final List<?> results = pair.getLeft();
                for (int i = 0; i < results.size(); i++) {
                    final T t = instances.get(i);
                    final Object o = results.get(i);
                    method.invoke(t, o);
                }
            }

            return instances;
        } catch (NoSuchMethodException e) {
            throw new ReportEngineReflectionException("Error obtaining constructor reference" , NO_METHOD_ERROR);
        } catch (InstantiationException e) {
            throw new ReportEngineReflectionException("Error instantiating an object", INSTANTIATION_ERROR);
        } catch (IllegalAccessException e) {
            throw new ReportEngineReflectionException("Error executing method does not have access to the definition of the specified class", ILLEGAL_ACCESS);
        } catch (InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error executing method does not have access to the definition of the specified class", INVOCATION_ERROR);
        }
    }

    private Configuration getClassReportConfiguration(Class<?> clazz) {
        final Report reportAnnotation = AnnotationUtils.getReportAnnotation(clazz);
        return AnnotationUtils.getReportConfiguration(reportAnnotation, reportName);
    }

    private <T> Map<Annotation, Pair<Class<?>, Method>> loadColumns(Class<T> clazz, Configuration configuration, boolean recursive) {

        Map<Column, Pair<Class<?>, Method>> columnsMap = new LinkedHashMap<>();
        final Function<Pair<Column, Pair<Class<?>, Method>>, Void> columnsFunction = AnnotationUtils.getColumnsWithFieldAndMethodsFunction(columnsMap);
        AnnotationUtils.columnsWithMethodAnnotations(clazz, columnsFunction, reportName);
        Map<Annotation, Pair<Class<?>, Method>> annotations = new LinkedHashMap<>(columnsMap);

        Map<Subreport, Pair<Class<?>, Method>> subreportsMap = new LinkedHashMap<>();
        final Function<Pair<Subreport, Pair<Class<?>, Method>>, Void> subreportsFunction = AnnotationUtils.getSubreportsWithFieldsAndMethodsFunction(subreportsMap);
        AnnotationUtils.subreportsWithFieldsAndMethodAnnotations(clazz, subreportsFunction, reportName);

        final SpecialColumn[] specialColumns = configuration.specialColumns();

        if(recursive) {
            for (Map.Entry<Subreport, Pair<Class<?>, Method>> entry : subreportsMap.entrySet()) {
                final Map<Annotation, Pair<Class<?>, Method>> map = loadColumns(entry.getValue().getLeft(), getClassReportConfiguration(entry.getValue().getLeft()), true);
                annotations.putAll(map);
            }
        } else {
            annotations.putAll(subreportsMap);
        }

        for (SpecialColumn specialColumn : specialColumns) {
            annotations.put(specialColumn, new Pair<>(null, null));
        }

        if(recursive){
            annotations = sortAnnotationsByPosition(annotations);
        }
        return annotations;
    }

    private static Map<Annotation, Pair<Class<?>, Method>> sortAnnotationsByPosition(Map<Annotation, Pair<Class<?>, Method>> map) {
        List<Map.Entry<Annotation, Pair<Class<?>, Method>>> list = new LinkedList<>(map.entrySet());
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

        Map<Annotation, Pair<Class<?>, Method>> result = new LinkedHashMap<>();
        for (Map.Entry<Annotation, Pair<Class<?>, Method>> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private <T> void instanceSetValueFromCell(final Method method, final Object instance, final Cell cell) throws ReportEngineReflectionException {
        method.setAccessible(true);
        try {
            if(cell != null){
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
        } catch (IllegalAccessException e) {
            throw new ReportEngineReflectionException("Error executing method does not have access to the definition of the specified class", ILLEGAL_ACCESS);
        } catch (InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error executing method does not have access to the definition of the specified class", INVOCATION_ERROR);
        }
    }

}
