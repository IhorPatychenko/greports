package org.greports.annotations;

import org.apache.commons.lang3.StringUtils;
import org.greports.engine.ValueType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation intended to indicate that an attribute
 * of the class is a subreport.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Repeatable(Subreport.List.class)
@Inherited
@Documented
public @interface Subreport {

    /**
     * Indicates the report's names which contains the subreport.
     *
     * @return {@link String}
     */
    String[] reportName();

    /**
     * Start position of columns in the subreport.
     * The position will be added to the position of internal columns
     * inside the subreport. These one will be ordered from lowest
     * to highest position index.
     *
     * @return float
     */
    float position();

    /**
     * Column ID which will be used by other column if that one
     * is of formula type. Also can be used by {@link SpecialColumn} and {@link SpecialRowCell}
     *
     * @see ValueType#FORMULA
     * @return {@link String}
     */
    String id() default StringUtils.EMPTY;

    /**
     * Defines several {@link Subreport} annotations on the same element.
     * @see Subreport
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @Inherited
    @Documented
    @interface List {
        Subreport[] value();
    }
}
