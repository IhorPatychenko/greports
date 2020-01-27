package utils;

import annotations.Column;
import annotations.Configuration;
import annotations.Report;
import annotations.SpecialColumn;
import annotations.Subreport;
import content.cell.ReportHeaderCell;
import engine.ReportColumn;
import exceptions.ReportEngineReflectionException;
import exceptions.ReportEngineRuntimeExceptionCode;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class AnnotationUtils {

    public static Configuration getClassReportConfiguration(Class<?> clazz, String reportName) {
        final Report reportAnnotation = AnnotationUtils.getReportAnnotation(clazz);
        return AnnotationUtils.getReportConfiguration(reportAnnotation, reportName);
    }

    public static Report getReportAnnotation(Class<?> clazz) {
        final Report annotation = clazz.getAnnotation(Report.class);
        if (annotation != null) {
            return annotation;
        }
        throw new ReportEngineReflectionException(clazz.toString() + " is not annotated as @Report", ReportEngineRuntimeExceptionCode.REPORT_ANNOTATION_NOT_FOUND);
    }

    public static Configuration getReportConfiguration(Report report, String reportName) {
        return Arrays.stream(report.reportConfigurations())
                .filter(entry -> entry.reportName().equals(reportName))
                .findFirst()
                .orElseThrow(() -> new ReportEngineReflectionException("@Report has no @ReportConfiguration annotation with name \"" + reportName + "\"", ReportEngineRuntimeExceptionCode.CONFIGURATION_ANNOTATION_NOT_FOUND));
    }

    public static int getLastSpecialRowsCount(Configuration configuration){
        return (int) Arrays.stream(configuration.specialRows())
                .filter(entry -> Integer.MAX_VALUE == entry.rowIndex()).count();
    }

    public static int getSpecialRowsCountBeforeData(Configuration configuration) {
        return configuration.specialRows().length - getLastSpecialRowsCount(configuration);
    }

    public static <T> void fieldsWithColumnAnnotations(Class<T> clazz, Function<Pair<Field, Column>, Void> columnFunction, String reportName) {
        for (Field declaredField : clazz.getDeclaredFields()) {
            final Column[] annotationsByType = declaredField.getAnnotationsByType(Column.class);
            for (Column generatorColumn : annotationsByType) {
                if (getReportColumnPredicate(reportName).test(generatorColumn)) {
                    columnFunction.apply(Pair.of(declaredField, generatorColumn));
                }
            }
        }
    }

    public static <T> void columnsWithMethodAnnotations(Class<T> clazz, Function<Pair<Column, Pair<Class<?>, Method>>, Void> function, String reportName) {
        for (Field declaredField : clazz.getDeclaredFields()) {
            final Column[] annotationsByType = declaredField.getAnnotationsByType(Column.class);
            for (Column column : annotationsByType) {
                if (getReportColumnPredicate(reportName).test(column)) {
                    final Pair<Column, Pair<Class<?>, Method>> columnPairPair = Pair.of(column, Pair.of(clazz, ReflectionUtils.fetchFieldSetter(declaredField, clazz)));
                    function.apply(columnPairPair);
                }
            }
        }
    }

    public static <T> void subreportsWithFieldsAndMethodAnnotations(Class<T> clazz, Function<Pair<Subreport, Pair<Class<?>, Method>>, Void> function, String reportName) {
        for (Field declaredField : clazz.getDeclaredFields()) {
            final Subreport[] annotationsByType = declaredField.getAnnotationsByType(Subreport.class);
            for (Subreport subreport : annotationsByType) {
                if (getSubreportPredicate(reportName).test(subreport)) {
                    final Method method = ReflectionUtils.fetchFieldSetter(declaredField, clazz);
                    final Pair<Subreport, Pair<Class<?>, Method>> columnPairPair = Pair.of(subreport, Pair.of(method.getParameterTypes()[0], method));
                    function.apply(columnPairPair);
                }
            }
        }
    }

    public static <T> Column getSubreportLastColumn(Class<T> clazz, String reportName){
        List<Column> list = new ArrayList<>();
        final Field[] fields = clazz.getDeclaredFields();
        for (final Field field : fields) {
            final Column[] columns = field.getAnnotationsByType(Column.class);
            for (final Column columnAnnotation : columns) {
                if (getReportColumnPredicate(reportName).test(columnAnnotation)) {
                    list.add(columnAnnotation);
                }
            }
        }
        return list.stream().max(Comparator.comparing(Column::position)).orElse(null);
    }

    public static <T> List<ReportColumn> loadAnnotations(final Class<T> clazz, String reportName, final boolean recursive) {
        return loadAnnotations(clazz, reportName, recursive, 0.0f);
    }

    private static <T> List<ReportColumn> loadAnnotations(final Class<T> clazz, String reportName, final boolean recursive, final float subreportIcrement) {
        List<ReportColumn> list = new ArrayList<>();
        final Field[] fields = clazz.getDeclaredFields();
        for (final Field field : fields) {
            final Subreport[] subreportsAnnotations = field.getAnnotationsByType(Subreport.class);
            for (final Subreport subreportsAnnotation : subreportsAnnotations) {
                Class<?> aClass;
                if(field.getType().equals(List.class)){
                    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                    aClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                } else {
                    aClass = field.getType();
                }
                if(recursive){
                    final List<ReportColumn> blockDtos = loadAnnotations(aClass, reportName, true, subreportsAnnotation.position());
                    list.addAll(blockDtos);
                } else {
                    if (getSubreportPredicate(reportName).test(subreportsAnnotation)) {
                        list.add(new ReportColumn(reportName, subreportsAnnotation, clazz, field, ReflectionUtils.fetchFieldSetter(field, clazz), subreportsAnnotation.position()));
                    }
                }
            }
            final Column[] columns = field.getAnnotationsByType(Column.class);
            for (final Column columnAnnotation : columns) {
                if (getReportColumnPredicate(reportName).test(columnAnnotation)) {
                    list.add(new ReportColumn(reportName, columnAnnotation, clazz, field, ReflectionUtils.fetchFieldSetter(field, clazz), columnAnnotation.position() + subreportIcrement));
                }
            }
        }
        Configuration configuration = getClassReportConfiguration(clazz, reportName);
        for (SpecialColumn specialColumn : configuration.specialColumns()) {
            list.add(new ReportColumn(reportName, specialColumn, clazz, null, null, specialColumn.position()));
        }
        list.sort(Comparator.comparing(ReportColumn::getAnnotationPosition));
        return list;
    }

    public static <T> void fieldsWithSubreportAnnotations(Class<T> clazz, Function<Pair<Field, Subreport>, Void> columnFunction, String reportName) {
        for (Field field : clazz.getDeclaredFields()) {
            final Subreport[] annotationsByType = field.getAnnotationsByType(Subreport.class);
            for (Subreport subreport : annotationsByType) {
                if (getSubreportPredicate(reportName).test(subreport)) {
                    columnFunction.apply(Pair.of(field, subreport));
                }
            }
        }
    }

    private static Predicate<Annotation> getReportColumnPredicate(String reportName) {
        return annotation -> ((Column) annotation).reportName().equals(reportName);
    }

    private static Predicate<Annotation> getSubreportPredicate(String reportName) {
        return annotation -> ((Subreport) annotation).reportName().equals(reportName);
    }



    public static Function<Pair<Field, Column>, Void> getFieldsAndColumnsFunction(Map<Field, Column> columnsMap){
        return pair -> {
            columnsMap.put(pair.getLeft(), pair.getRight());
            return null;
        };
    }

    public static Function<Pair<Column, Pair<Class<?>, Method>>, Void> getColumnsWithFieldAndMethodsFunction(Map<Column, Pair<Class<?>, Method>> columnPairMap){
        return pair -> {
            columnPairMap.put(pair.getLeft(), pair.getRight());
            return null;
        };
    }

    public static Function<Pair<Subreport, Pair<Class<?>, Method>>, Void> getSubreportsWithFieldsAndMethodsFunction(Map<Subreport, Pair<Class<?>, Method>> subreportsMap) {
        return pair -> {
            subreportsMap.put(pair.getLeft(), pair.getRight());
            return null;
        };
    }

    public static Function<Pair<Field, Column>, Void> getHeadersFunction(List<ReportHeaderCell> cells, Map<String, Object> translations, Float positionIncrement){
        return pair -> {
            Column column = pair.getRight();
            cells.add(new ReportHeaderCell(column.position() + positionIncrement, (String) translations.getOrDefault(column.title(), column.title()), column.id(), column.autoSizeColumn()));
            return null;
        };
    }

    public static Function<Pair<Field, Subreport>, Void> getSubreportsFunction(Map<Field, Subreport> subreportMap){
        return pair -> {
            subreportMap.put(pair.getLeft(), pair.getRight());
            return null;
        };
    }
}
