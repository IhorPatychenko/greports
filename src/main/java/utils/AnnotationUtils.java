package utils;

import annotations.Report;
import annotations.ReportColumn;
import annotations.ReportColumns;
import annotations.ReportConfiguration;
import annotations.ReportLoaderColumn;
import annotations.ReportLoaderColumns;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class AnnotationUtils {

    public static <T> ReportConfiguration getReportConfiguration(Report report, String reportName) {
        if(Objects.isNull(report)) return null;
        return Arrays.stream(report.reportConfigurations())
                .filter(entry -> entry.name().equals(reportName))
                .findFirst()
                .orElse(null);
    }

    public static <T> Report getReportAnnotation(Class<T> clazz) {
        return Arrays.stream(clazz.getAnnotationsByType(Report.class)).findFirst().orElse(null);
    }

    public static <T> void checkReportConfiguration(ReportConfiguration reportConfiguration, Class<T> clazz, String reportName) throws Exception {
        if (Objects.isNull(reportConfiguration)) {
            throw new Exception(clazz.toString() + " is not annotated as @Report or has no @ReportConfiguration with name \"" + reportName + "\"");
        }
    }

    public static <T> void reportGeneratorMethodsWithColumnAnnotations(Class<T> clazz, Function<AbstractMap.SimpleEntry<Method, ReportColumn>, Void> columnFunction, Predicate<Annotation> predicate){
        for (Method method : clazz.getMethods()) {
            for (Annotation annotation : method.getDeclaredAnnotations()) {
                if(annotation instanceof ReportColumn && predicate.test(annotation)){
                    columnFunction.apply(new AbstractMap.SimpleEntry<>(method, (ReportColumn) annotation));
                } else if(annotation instanceof ReportColumns){
                    Optional<ReportColumn> first = Arrays.stream(((ReportColumns) annotation).value())
                            .filter(predicate)
                            .findFirst();
                    first.ifPresent(column -> columnFunction.apply(new AbstractMap.SimpleEntry<>(method, column)));
                }
            }
        }
    }

    public static <T> void reportLoaderMethodsWithColumnAnnotations(Class<T> clazz, Function<AbstractMap.SimpleEntry<Method, ReportLoaderColumn>, Void> columnFunction, Predicate<Annotation> predicate){
        for (Method method : clazz.getMethods()) {
            for (Annotation annotation : method.getDeclaredAnnotations()) {
                if(annotation instanceof ReportLoaderColumn && predicate.test(annotation)){
                    columnFunction.apply(new AbstractMap.SimpleEntry<>(method, (ReportLoaderColumn) annotation));
                } else if(annotation instanceof ReportLoaderColumns){
                    Optional<ReportLoaderColumn> first = Arrays.stream(((ReportLoaderColumns) annotation).value())
                            .filter(predicate)
                            .findFirst();
                    first.ifPresent(column -> columnFunction.apply(new AbstractMap.SimpleEntry<>(method, column)));
                }
            }
        }
    }

    public static Predicate<Annotation> getReportColumnsPredicate(String reportName) {
        return annotation -> annotation instanceof ReportColumn
                && ((ReportColumn) annotation).reportName().equals(reportName);
    }

    public static Predicate<Annotation> getReportLoaderColumnsPredicate(String reportName) {
        return annotation -> annotation instanceof ReportLoaderColumn
                && ((ReportLoaderColumn) annotation).reportName().equals(reportName);
    }
}
