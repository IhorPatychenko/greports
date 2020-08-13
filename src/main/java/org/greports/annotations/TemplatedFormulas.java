package org.greports.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface TemplatedFormulas {

    /**
     * Indicates the report's name which contains the column.
     *
     * @return {@link String}
     */
    String[] reportName();

    /**
     * Column title. This text string will be used to search for
     * the corresponding translation in the translation file located in
     * the directory provided by the {@link Configuration#translationsDir()}
     *
     * @return {@link String}
     */
    String title() default "";

    /**
     * Maximum count of formula columns to evaluate.
     * Example: 1,3,6.
     * The value -1 indicates that the engine will try
     * to evaluate all the columns he finds
     *
     * @return int
     */
    int max() default -1;

}
