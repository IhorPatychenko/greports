package engine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ReportColumn {
    private String reportName;
    private Annotation annotation;
    private Class<?> parentClass;
    private Field field;

    public ReportColumn(String reportName, final Annotation annotation, final Class<?> parentClass, final Field field) {
        this.reportName = reportName;
        this.annotation = annotation;
        this.parentClass = parentClass;
        this.field = field;
    }

    public String getReportName() {
        return reportName;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public Class<?> getParentClass() {
        return parentClass;
    }

    public Field getField() {
        return field;
    }

    public Class<?> getFieldClass() {
        return field.getType();
    }

    public Class<? extends Annotation> getAnnotationClass(){
        return annotation.getClass();
    }
}
