package utils;

import annotations.Report;
import annotations.ReportColumn;
import annotations.ReportColumns;
import content.ReportData;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.asList;

public class AnnotationUtils {

    public static <T> Report getReportAnnotation(String reportName, Class<T> clazz) {
        Annotation[] classAnnotations = clazz.getAnnotations();
        List<Annotation> dtoAnnotations = asList(classAnnotations);
        return (Report) (dtoAnnotations.stream()
                .filter(entry -> entry instanceof Report && asList(((Report) entry).name()).contains(reportName))
                .findFirst()
                .orElse(null));
    }

    public static <T> void checkReportAnnotation(Report reportAnnotation, Class<T> clazz, String reportName) throws Exception {
        if (Objects.isNull(reportAnnotation)) {
            throw new Exception(clazz.toString() + " is not annotated as @Report or has no name \"" + reportName + "\"");
        } else if(reportAnnotation.headerOffset() >= reportAnnotation.dataOffset()){
            throw new Exception("Header offset cannot be greater or equals than data offset");
        }
    }

    public static <T> void loadMethodsColumns(Class<T> clazz, Function<AbstractMap.SimpleEntry<Method, ReportColumn>, Void> columnFunction, String reportName){
        loadMethodsColumns(clazz, columnFunction, reportName, false);
    }

    public static <T> void loadMethodsColumns(Class<T> clazz, Function<AbstractMap.SimpleEntry<Method, ReportColumn>, Void> columnFunction, String reportName, boolean columnForLoader){
        for (Method method : clazz.getMethods()) {
            for (Annotation annotation : method.getDeclaredAnnotations()) {
                if(annotation instanceof ReportColumn && getMethodAnnotationPredicate(reportName, columnForLoader).test(annotation)){
                    columnFunction.apply(new AbstractMap.SimpleEntry<>(method, (ReportColumn) annotation));
                } else if(annotation instanceof ReportColumns){
                    Optional<ReportColumn> first = Arrays.stream(((ReportColumns) annotation).value())
                            .filter(column -> getMethodAnnotationPredicate(reportName, columnForLoader).test(column))
                            .findFirst();
                    first.ifPresent(column -> columnFunction.apply(new AbstractMap.SimpleEntry<>(method, column)));
                }
            }
        }
    }

    private static Predicate<Annotation> getMethodAnnotationPredicate(String reportName, boolean columnForLoader) {
        return annotation -> annotation instanceof ReportColumn
                && ((ReportColumn) annotation).reportName().equals(reportName)
                && ((ReportColumn) annotation).columnForLoader() == columnForLoader;
    }
}
