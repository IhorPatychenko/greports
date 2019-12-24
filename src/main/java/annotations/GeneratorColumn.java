package annotations;

import engine.ValueType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Repeatable(value = GeneratorColumns.class)
public @interface GeneratorColumn {
    String reportName();
    float position();
    String title() default "";
    String format() default "";
    String id() default "";
    String[] targetIds() default {};
    ValueType valueType() default ValueType.LITERAL;
    boolean autoSizeColumn() default false;
}