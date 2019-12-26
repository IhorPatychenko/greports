package annotations;

import engine.ValueType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Repeatable(value = Columns.class)
public @interface Column {
    String reportName();
    float position();
    String title() default "";
    String format() default "";
    String id() default "";
    String[] targetIds() default {};
    ValueType valueType() default ValueType.LITERAL;
    boolean autoSizeColumn() default false;
}
