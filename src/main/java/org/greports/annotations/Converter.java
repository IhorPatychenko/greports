package org.greports.annotations;

import org.greports.converters.AbstractValueConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface Converter {

    /**
     * Converter class
     * @return {@link Class<AbstractValueConverter>}
     */
    Class<? extends AbstractValueConverter> converterClass();

    /**
     * Array of params to be passed to the converter
     * @return {@link String[]}
     */
    String[] params() default {};

}
