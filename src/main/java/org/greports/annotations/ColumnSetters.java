package org.greports.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface ColumnSetters {

    /**
     * @return an array of {@link ColumnSetter}
     */
    ColumnSetter[] value();
}
