package utils;

import annotations.Report;
import annotations.ReportGeneratorColumn;
import annotations.ReportConfiguration;
import annotations.ReportLoaderColumn;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public class AnnotationUtils {

    public static <T> ReportConfiguration getReportConfiguration(Report report, String reportName) {
        return Arrays.stream(report.reportConfigurations())
                .filter(entry -> entry.name().equals(reportName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("@Report has no @ReportConfiguration annotation with name \"" + reportName + "\""));
    }

    public static Report getReportAnnotation(Class clazz) {
        final Annotation annotation = clazz.getAnnotation(Report.class);
        if(annotation != null){
            return (Report) annotation;
        }
        throw new RuntimeException(clazz.toString() + " is not annotated as @Report");
    }

    public static <T> void reportGeneratorMethodsWithColumnAnnotations(Class<T> clazz, Function<AbstractMap.SimpleEntry<Method, ReportGeneratorColumn>, Void> columnFunction, String reportName){
        for (Method method : clazz.getMethods()) {
            final ReportGeneratorColumn[] annotationsByType = method.getAnnotationsByType(ReportGeneratorColumn.class);
            for (ReportGeneratorColumn reportGeneratorColumn : annotationsByType) {
                if(getReportGeneratorColumnsPredicate(reportName).test(reportGeneratorColumn)){
                    columnFunction.apply(new AbstractMap.SimpleEntry<>(method, reportGeneratorColumn));
                }
            }
        }
    }

    public static <T> void reportLoaderMethodsWithColumnAnnotations(Class<T> clazz, Function<AbstractMap.SimpleEntry<Method, ReportLoaderColumn>, Void> columnFunction, String reportName){
        for (Method method : clazz.getMethods()) {
            final ReportLoaderColumn[] annotationsByType = method.getAnnotationsByType(ReportLoaderColumn.class);
            for (ReportLoaderColumn reportLoaderColumn : annotationsByType) {
                if(getReportLoaderColumnsPredicate(reportName).test(reportLoaderColumn)){
                    columnFunction.apply(new AbstractMap.SimpleEntry<>(method, reportLoaderColumn));
                }
            }
        }
    }

    private static Predicate<Annotation> getReportGeneratorColumnsPredicate(String reportName) {
        return annotation -> ((ReportGeneratorColumn) annotation).reportName().equals(reportName);
    }

    private static Predicate<Annotation> getReportLoaderColumnsPredicate(String reportName) {
        return annotation -> ((ReportLoaderColumn) annotation).reportName().equals(reportName);
    }
}
