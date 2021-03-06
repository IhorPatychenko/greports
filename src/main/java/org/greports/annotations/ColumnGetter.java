package org.greports.annotations;

import org.apache.commons.lang3.StringUtils;
import org.greports.converters.NotImplementedConverter;
import org.greports.engine.ValueType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Repeatable(ColumnGetter.List.class)
@Inherited
@Documented
public @interface ColumnGetter {
    /**
     * Indicates the report's name which contains the column.
     *
     * @return {@link String}
     */
    String[] reportName();

    /**
     * Position of column in the report.
     * The columns will be ordered from lowest to highest position index.
     * In case of need to add a new column among the existing ones,
     * the floating part can be used to indicate the desired position.
     *
     * @return float
     */
    float position();

    /**
     * The value used to target attributes in a nested class.
     * @return {@link String}
     */
    String target() default StringUtils.EMPTY;

    /**
     * Column title. This text string will be used to search for
     * the corresponding translation in the translation file located in
     * the directory provided by the {@link Configuration#translationsDir()}
     *
     * @return {@link String}
     */
    String title() default StringUtils.EMPTY;

    /**
     * A converter to be applied when the column value is being obtained.
     *
     * @return {@link Converter}
     */
    Converter typeConverter() default @Converter(converterClass = NotImplementedConverter.class);

    /**
     * Visualisation format to be displayed.
     * You can find information on how to create your own org.greports.styles
     * by going through this <a href="http://poi.apache.org/components/spreadsheet/quick-guide.html#DataFormats">link</a>
     *
     * @return {@link String}
     */
    String format() default StringUtils.EMPTY;

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
    String id() default StringUtils.EMPTY;

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

    /**
     * The value of this attribute allows to enable the translation
     * of the cell content.
     * @return {@code boolean}
     */
    boolean translate() default false;

    /**
     * Defines several {@link ColumnGetter} annotations on the same element.
     * @see ColumnGetter
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Inherited
    @Documented
    @interface List {
        /**
         * @return {@link ColumnGetter} array
         */
        ColumnGetter[] value();
    }

}
