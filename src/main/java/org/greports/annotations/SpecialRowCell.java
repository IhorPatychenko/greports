package org.greports.annotations;

import org.apache.commons.lang3.StringUtils;
import org.greports.engine.ValueType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface SpecialRowCell {

    /**
     * A target id used to dynamically generate formulas.
     * There cannot be 2 o more SpecialRowCells with empty targetId value.
     *
     * @return {@link String}
     */
    String targetId() default StringUtils.EMPTY;

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
    String value() default StringUtils.EMPTY;

    /**
     * Visualisation format to be applied.
     * You can find information on how to create your own org.greports.styles
     * by going through this <a href="http://poi.apache.org/components/spreadsheet/quick-guide.html#DataFormats">link</a>
     *
     * @return {@link String}
     */
    String format() default StringUtils.EMPTY;

    /**
     * A cell comment text.
     * @return {@link String}
     */
    String comment() default StringUtils.EMPTY;

    /**
     * Default comment width in columns.
     * @return {@code short}
     */
    short commentWidth() default 2;

    /**
     * Default comment height in rows.
     * @return {@code short}
     */
    short commentHeight() default 2;

    /**
     * This value indicates the number of merged cells.
     * A count of merged cells needs to be grater o equals than 1.
     * If the value is greater than 1 means that the column will merge
     * cells of his right. Example:
     * position = 1 (first column), columnWidth = 2. In this case
     * the row will have the cells A1 and B1 merged in only one cell
     * which will be placed into A1 cell.
     * @return int
     */
    int columnWidth() default 1;
}
