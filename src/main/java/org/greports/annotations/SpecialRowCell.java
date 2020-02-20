package org.greports.annotations;

import org.greports.engine.ValueType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface SpecialRowCell {

    /**
     * A column target id to place the SpecialRowCell above of below.
     *
     * @return {@link String}
     */
    String targetId();

    /**
     * The {@link ValueType} of the column.
     *
     * @return {@link ValueType}
     */
    ValueType valueType() default ValueType.PLAIN_VALUE;

    /**
     * Value of the cell with data to show
     *
     * @return {@link String}
     */
    String value();

    /**
     * Visualisation format to be applied.
     * You can find information on how to create your own org.greports.styles
     * by going through this <a href="http://poi.apache.org/components/spreadsheet/quick-guide.html#DataFormats">link</a>
     *
     * @return {@link String}
     */
    String format() default "";
}
