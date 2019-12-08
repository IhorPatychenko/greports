package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ReportSpecialCell {

    enum ValueType {
        LITERAL, FORMULA
    }

    String targetId() default "";
    ValueType valueType() default ValueType.LITERAL;
    String value() default "";
}
