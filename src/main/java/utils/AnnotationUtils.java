package utils;

import annotations.Column;
import annotations.Configuration;
import annotations.Report;
import annotations.SpecialColumn;
import annotations.Subreport;
import content.cell.ReportHeaderCell;
import engine.ReportBlock;
import exceptions.ReportEngineReflectionException;
import exceptions.ReportEngineRuntimeExceptionCode;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
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
                .orElseThrow(() -> new ReportEngineReflectionException(String.format("@Report has no @ReportConfiguration annotation with name \"%s\"", reportName), ReportEngineRuntimeExceptionCode.CONFIGURATION_ANNOTATION_NOT_FOUND));
    }

    public static int getLastSpecialRowsCount(Configuration configuration) {
        return (int) Arrays.stream(configuration.specialRows())
                .filter(entry -> Integer.MAX_VALUE == entry.rowIndex()).count();
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

    public static <T> Column getSubreportLastColumn(Class<T> clazz, String reportName) {
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

    public static <T> Map<Annotation, Field> loadBlockAnnotations(final ReportBlock reportBlock) {
        Map<Annotation, Field> map = new HashMap<>();
        Class<?> clazz = reportBlock.getBlockClass();
        final Field[] fields = clazz.getDeclaredFields();
        for (final Field field : fields) {
            Arrays.stream(field.getAnnotationsByType(Subreport.class))
                    .filter(subreport -> subreport.reportName().equals(reportBlock.getReportName()))
                    .findFirst()
                    .ifPresent(subreportAnnotation -> map.put(subreportAnnotation, field));

            Arrays.stream(field.getAnnotationsByType(Column.class))
                    .filter(column -> column.reportName().equals(reportBlock.getReportName()))
                    .findFirst()
                    .ifPresent(columnAnnotation -> map.put(columnAnnotation, field));
        }

        Configuration configuration = getClassReportConfiguration(clazz, reportBlock.getReportName());
        for (SpecialColumn specialColumn : configuration.specialColumns()) {
            map.put(specialColumn, null);
        }
        return map;
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

    public static Function<Pair<Field, Column>, Void> getFieldsAndColumnsFunction(Map<Field, Column> columnsMap) {
        return pair -> {
            columnsMap.put(pair.getLeft(), pair.getRight());
            return null;
        };
    }


    public static Function<Pair<Field, Column>, Void> getHeadersFunction(List<ReportHeaderCell> cells, Map<String, Object> translations, Float positionIncrement) {
        return pair -> {
            Column column = pair.getRight();
            cells.add(new ReportHeaderCell(column.position() + positionIncrement, (String) translations.getOrDefault(column.title(), column.title()), column.id(), column.autoSizeColumn()));
            return null;
        };
    }

    public static Function<Pair<Field, Subreport>, Void> getSubreportsFunction(Map<Field, Subreport> subreportMap) {
        return pair -> {
            subreportMap.put(pair.getLeft(), pair.getRight());
            return null;
        };
    }
}
