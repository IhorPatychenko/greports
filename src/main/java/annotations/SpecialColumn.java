package annotations;

import engine.ValueType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface SpecialColumn {
    String reportName();
    float position();
    String title() default "";
    String value();
    String format() default "";
    String[] targetIds() default {};
    boolean isRangedFormula() default false;
    ValueType valueType() default ValueType.LITERAL;
    boolean autoSizeColumn() default false;
}
