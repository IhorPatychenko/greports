package org.greports.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface ColumnGetters {
    /**
     * @return an array of {@link ColumnGetter}
     */
    ColumnGetter[] value();
}
