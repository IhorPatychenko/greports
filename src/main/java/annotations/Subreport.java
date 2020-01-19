package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation intended to indicate that an attribute
 * of the class is a subreport.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Repeatable(value = Subreports.class)
public @interface Subreport {

    /**
     * Indicates the report's name which contains the subreport.
     *
     * @return {@link String}
     */
    String reportName();

    /**
     * The positional increment indicates what increment should have
     * every next subreport if the parent report contains more than one
     * subreport of the same class and the column of the same subreport needs
     * to be grouped. This increment will added to every column of the subreport.
     * To have the sub-column columns grouped, the positional value
     * must be assigned the value that exceeds the subtraction between
     * the highest and lowest positions of the subreport's columns.
     *
     * @return float
     */
    float positionIncrement() default 0.0f;
}
