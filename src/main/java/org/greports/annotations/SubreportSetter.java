package org.greports.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Repeatable(value = SubreportSetters.class)
public @interface SubreportSetter {

    /**
     * Indicates the report's names which contains the subreport.
     *
     * @return {@link String}
     */
    String[] reportName();

    /**
     * Start position of columns in the subreport.
     * The position will be added to the position of internal columns
     * inside the subreport. These one will be ordered from lowest
     * to highest position index.
     *
     * @return float
     */
    float position();
}
