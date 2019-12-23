package annotations;

import engine.ValueType;
import formula.Formula;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ReportSpecialRowCell {

    String targetId();

    /**
     * @return one the possible values for value type of the cell containing special data
     */
    ValueType valueType() default ValueType.LITERAL;

    /**
     * @return java.lang.String value of the cell with special data
     */
    Formula value();

    /**
     * @return java.lang.String representation format
     */
    String format() default "";

}
