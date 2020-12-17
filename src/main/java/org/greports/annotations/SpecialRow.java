package org.greports.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Data special row. Can be used to insert data like totals and other formulas.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface SpecialRow {

    /**
     * Special row index. Needs to be greater or equals than zero.
     *
     * @return int
     */
    int rowIndex();

    /**
     * This value indicates if the row row needs t be sticky (freeze).
     *
     * @return {@code boolean}
     */
    boolean stickyRow() default false;

    /**
     * An array of {@link SpecialRowCell}
     *
     * @return an array of {@link SpecialRowCell} with cells containing special data in the report
     */
    SpecialRowCell[] cells() default {};
}
