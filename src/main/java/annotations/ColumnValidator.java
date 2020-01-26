package annotations;

import validators.AbstractColumnValidator;
import validators.AbstractValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ColumnValidator {

    /**
     * The class to obtain in order to pass the validation.
     * The class must extend from the {@link AbstractValidator} class
     * and implement the methods with logic
     * to perform the validation of the report class attribute.
     *
     * @return {@link Class}
     */
    Class<? extends AbstractColumnValidator> validatorClass();

    /**
     * The constant value that is passed to the validator builder.
     * Because of the Java annotation restrictions,
     * the value of this cannot be of the {@link Object} type,
     * you must make the necessary value conversion
     * of the {@link String} type to the data type you need.
     *
     * @return {@link String}
     */
    String param() default "";

    /**
     * ID of column to be evaluated against the validator.
     * @return {@link String}
     */
    String targetId();

    /**
     * Error message. This text string will be used to search for
     * the corresponding translation in the translation file located in
     * the directory provided by the {@link Configuration#translationsDir()}
     *
     * @return {@link String}
     */
    String errorMessage() default "";
}
