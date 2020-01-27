package engine;

import annotations.SpecialColumn;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReportColumn {
    private String reportName;
    private Annotation annotation;
    private Class<?> parentClass;
    private Field field;
    private Method method;
    private Float annotationPosition;

    public ReportColumn(String reportName, final Annotation annotation, final Class<?> parentClass, final Field field, final Method fieldMethod, final Float annotationPosition) {
        this.reportName = reportName;
        this.annotation = annotation;
        this.parentClass = parentClass;
        this.field = field;
        this.method = fieldMethod;
        this.annotationPosition = annotationPosition;
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

    public Method getMethod() {
        return method;
    }

    public Float getAnnotationPosition() {
        return annotationPosition;
    }

    public boolean isSpecialColumn(){
        return annotation instanceof SpecialColumn;
    }
}
