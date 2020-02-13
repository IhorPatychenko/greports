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

    float position();
}
