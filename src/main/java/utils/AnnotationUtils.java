package utils;

import annotations.Report;
import annotations.GeneratorColumn;
import annotations.Configuration;
import annotations.LoaderColumn;
import annotations.GeneratorSubreport;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public class AnnotationUtils {

    public static Configuration getReportConfiguration(Report report, String reportName) {
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

    public static <T> void generatorMethodsWithColumnAnnotations(Class<T> clazz, Function<AbstractMap.SimpleEntry<Method, GeneratorColumn>, Void> columnFunction, String reportName){
        for (Method method : clazz.getDeclaredMethods()) {
            final GeneratorColumn[] annotationsByType = method.getAnnotationsByType(GeneratorColumn.class);
            for (GeneratorColumn generatorColumn : annotationsByType) {
                if(getReportGeneratorColumnsPredicate(reportName).test(generatorColumn)){
                    columnFunction.apply(new AbstractMap.SimpleEntry<>(method, generatorColumn));
                }
            }
        }
    }

    public static <T> void loaderMethodsWithColumnAnnotations(Class<T> clazz, Function<AbstractMap.SimpleEntry<Method, LoaderColumn>, Void> columnFunction, String reportName){
        for (Method method : clazz.getDeclaredMethods()) {
            final LoaderColumn[] annotationsByType = method.getAnnotationsByType(LoaderColumn.class);
            for (LoaderColumn loaderColumn : annotationsByType) {
                if(getReportLoaderColumnsPredicate(reportName).test(loaderColumn)){
                    columnFunction.apply(new AbstractMap.SimpleEntry<>(method, loaderColumn));
                }
            }
        }
    }

    public static <T> void generatorMethodWithSubreportAnnotations(Class<T> clazz, Function<AbstractMap.SimpleEntry<Method, GeneratorSubreport>, Void> columnFunction, String reportName) {
        for (Method method : clazz.getDeclaredMethods()) {
            final GeneratorSubreport[] annotationsByType = method.getAnnotationsByType(GeneratorSubreport.class);
            for (GeneratorSubreport generatorSubreport : annotationsByType) {
                if(getSubreportPredicate(reportName).test(generatorSubreport)){
                    columnFunction.apply(new AbstractMap.SimpleEntry<>(method, generatorSubreport));
                }
            }
        }
    }

    private static Predicate<Annotation> getReportGeneratorColumnsPredicate(String reportName) {
        return annotation -> ((GeneratorColumn) annotation).reportName().equals(reportName);
    }

    private static Predicate<Annotation> getReportLoaderColumnsPredicate(String reportName) {
        return annotation -> ((LoaderColumn) annotation).reportName().equals(reportName);
    }

    private static Predicate<Annotation> getSubreportPredicate(String reportName) {
        return annotation -> ((GeneratorSubreport) annotation).reportName().equals(reportName);
    }
}
