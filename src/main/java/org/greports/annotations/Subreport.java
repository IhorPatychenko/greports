package org.greports.annotations;

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

    float position();
}
