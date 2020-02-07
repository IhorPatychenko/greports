package org.greports.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that is used to indicate that a class is part of a report.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Report {
    /**
     * An array of {@link Configuration}
     *
     * @return an array of {@link Configuration}
     */
    Configuration[] reportConfigurations();
}
