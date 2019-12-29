package utils;

import annotations.Column;
import annotations.Report;
import annotations.Configuration;
import annotations.Subreport;
import content.cell.ReportHeaderCell;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class AnnotationUtils {

    private static final List<String> gettersPrefixes = new ArrayList<>(Arrays.asList("get", "is"));
    private static final List<String> settersPrefixes = new ArrayList<>(Arrays.asList("set"));

    public static Configuration getReportConfiguration(Report report, String reportName) {
        return Arrays.stream(report.reportConfigurations())
                .filter(entry -> entry.reportName().equals(reportName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("@Report has no @ReportConfiguration annotation with name \"" + reportName + "\""));
    }

    public static Report getReportAnnotation(Class clazz) {
        final Annotation annotation = clazz.getAnnotation(Report.class);
        if (annotation != null) {
            return (Report) annotation;
        }
        throw new RuntimeException(clazz.toString() + " is not annotated as @Report");
    }

    public static <T> void fieldsWithColumnAnnotations(Class<T> clazz, Function<Pair<Field, Column>, Void> columnFunction, String reportName) {
        for (Field declaredField : clazz.getDeclaredFields()) {
            final Column[] annotationsByType = declaredField.getAnnotationsByType(Column.class);
            for (Column generatorColumn : annotationsByType) {
                if (getReportColumnPredicate(reportName).test(generatorColumn)) {
                    columnFunction.apply(new Pair<>(declaredField, generatorColumn));
                }
            }
        }
    }

    public static <T> void columnsWithMethodAnnotations(Class<T> clazz, Function<Pair<Column, Pair<Class, Method>>, Void> function, String reportName) throws NoSuchMethodException {
        for (Field declaredField : clazz.getDeclaredFields()) {
            final Column[] annotationsByType = declaredField.getAnnotationsByType(Column.class);
            for (Column column : annotationsByType) {
                if (getReportColumnPredicate(reportName).test(column)) {
                    final Pair<Column, Pair<Class, Method>> columnPairPair = new Pair<>(column, new Pair<>(clazz, fetchFieldSetter(declaredField, clazz)));
                    function.apply(columnPairPair);
                }
            }
        }
    }

    public static <T> void subreportsWithFieldsAndMethodAnnotations(Class<T> clazz, Function<Pair<Subreport, Pair<Class, Method>>, Void> function, String reportName) throws NoSuchMethodException {
        for (Field declaredField : clazz.getDeclaredFields()) {
            final Subreport[] annotationsByType = declaredField.getAnnotationsByType(Subreport.class);
            for (Subreport subreport : annotationsByType) {
                if (getSubreportPredicate(reportName).test(subreport)) {
                    final Method method = fetchFieldSetter(declaredField, clazz);
                    final Pair<Subreport, Pair<Class, Method>> columnPairPair = new Pair<>(subreport, new Pair<>(method.getParameterTypes()[0], method));
                    function.apply(columnPairPair);
                }
            }
        }
    }

    public static <T> void fieldsWithSubreportAnnotations(Class<T> clazz, Function<Pair<Field, Subreport>, Void> columnFunction, String reportName) {
        for (Field field : clazz.getDeclaredFields()) {
            final Subreport[] annotationsByType = field.getAnnotationsByType(Subreport.class);
            for (Subreport subreport : annotationsByType) {
                if (getSubreportPredicate(reportName).test(subreport)) {
                    columnFunction.apply(new Pair<>(field, subreport));
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

    public static <T> Method fetchFieldGetter(Field field, Class<T> clazz) throws NoSuchMethodException {
        List<String> getterPossibleNames = new ArrayList<>();
        for (String gettersPrefix : gettersPrefixes) {
            getterPossibleNames.add(gettersPrefix + Utils.capitalizeString(field.getName()));
        }
        for (String getterPossibleName : getterPossibleNames) {
            try {
                final Method method = clazz.getMethod(getterPossibleName);
                if (method != null) {
                    return method;
                }
            } catch (NoSuchMethodException ignored) {
            }
        }
        throw new NoSuchMethodException("No getter was found with any of these names \"" + String.join(", ", getterPossibleNames) + "\" for field " + field.getName() + " in class @" + clazz.getSimpleName());
    }

    public static <T> Method fetchFieldSetter(Field field, Class<T> clazz) throws NoSuchMethodException {
        List<String> setterPossibleNames = new ArrayList<>();
        for (String settersPrefix : settersPrefixes) {
            setterPossibleNames.add(settersPrefix + Utils.capitalizeString(field.getName()));
        }
        for (String setterPossibleName : setterPossibleNames) {
            try {
                final Method method = clazz.getMethod(setterPossibleName, field.getType());
                if (method != null) {
                    return method;
                }
            } catch (NoSuchMethodException ignored) {
            }
        }
        throw new NoSuchMethodException("No setter was found with any of these names \"" + String.join(", ", setterPossibleNames) + "\" for field " + field.getName());
    }

    public static Function<Pair<Field, Column>, Void> getFieldsAndColumnsFunction(Map<Field, Column> columnsMap){
        return pair -> {
            columnsMap.put(pair.getLeft(), pair.getRight());
            return null;
        };
    }

    public static Function<Pair<Column, Pair<Class, Method>>, Void> getColumnsWithFieldAndMethodsFunction(Map<Column, Pair<Class, Method>> columnPairMap){
        return pair -> {
            final Column column = pair.getLeft();
            final Pair<Class, Method> pairFieldMethod = pair.getRight();
            final Class clazz = pairFieldMethod.getLeft();
            final Method method = pairFieldMethod.getRight();
            columnPairMap.put(column, new Pair<>(clazz, method));
            return null;
        };
    }

    public static Function<Pair<Subreport, Pair<Class, Method>>, Void> getSubreportsWithFieldsAndMethodsFunction(Map<Subreport, Pair<Class, Method>> subreportsMap) {
        return pair -> {
            final Subreport subreport = pair.getLeft();
            final Pair<Class, Method> pairFieldMethod = pair.getRight();
            final Class clazz = pairFieldMethod.getLeft();
            final Method method = pairFieldMethod.getRight();
            subreportsMap.put(subreport, new Pair<>(clazz, method));
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
