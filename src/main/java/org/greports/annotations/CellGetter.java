package org.greports.annotations;

import org.greports.engine.ValueType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Repeatable(value = CellGetters.class)
public @interface CellGetter {
    /**
     * Indicates the report's name which contains the column.
     *
     * @return {@link String}
     */
    String[] reportName();

    /**
     * Row index. Zero based value.
     *
     * @return int
     */
    int row();

    /**
     * Column index. Zero based value.
     *
     * @return int
     */
    int column();

    /**
     * An array of {@link CellValidator} to be applied to the column.
     *
     * @return {@link CellValidator}
     */
    CellValidator[] cellValidators() default {};

    /**
     * An array of {@link Converter} to be applied when the column value is
     * being obtained. The array cannot contain more than 1 converter
     * because its not possible to know which one needs to be used to
     * convert the column value.
     *
     * @return Converter[]
     */
    Converter[] getterConverter() default {};

    /**
     * Visualisation format to be displayed.
     * You can find information on how to create your own org.greports.styles
     * by going through this <a href="http://poi.apache.org/components/spreadsheet/quick-guide.html#DataFormats">link</a>
     *
     * @return {@link String}
     */
    String format() default "";

    /**
     * The {@link ValueType} of the column.
     *
     * @return {@link ValueType}
     */
    ValueType valueType() default ValueType.PLAIN_VALUE;

    /**
     * Column ID which will be used by other column if that one
     * is of formula type. Also can be used by {@link SpecialColumn} and {@link SpecialRowCell}
     *
     * @see ValueType#FORMULA
     * @return {@link String}
     */
    String id() default "";

    /**
     * The value indicates whether the column has to fit the width of the longest cell.
     * This functionality is very expensive due to the large number of calculations to be performed.
     * Use only when necessary.
     *
     * @return boolean
     */
    boolean autoSizeColumn() default false;

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
