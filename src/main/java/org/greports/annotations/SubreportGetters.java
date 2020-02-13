package org.greports.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubreportGetters {
    /**
     * @return an array of {@link SubreportGetter}
     */
    SubreportGetter[] value();
}
