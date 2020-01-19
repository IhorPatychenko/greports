package annotations;

import validators.AbstractValidator;

/**
 * Annotation indicating the validation that
 * the report attribute has to pass when loading an .xlsx file.
 */
public @interface Validator {

    /**
     * The class to obtain in order to pass the validation.
     * The class must extend from the {@link AbstractValidator} class
     * and implement the methods with logic
     * to perform the validation of the report class attribute.
     *
     * @return {@link Class}
     */
    Class<? extends AbstractValidator> validatorClass();

    /**
     * The constant value that is passed to the validator builder.
     * Because of the Java annotation restrictions,
     * the value of this cannot be of the {@link Object} type,
     * you must make the necessary conserving of the value
     * of the {@link String} type to the type of data you need.
     *
     * @return {@link String}
     */
    String value() default "";

    /**
     * Error message. This text string will be used to search for
     * the corresponding translation in the translation file located in
     * the directory provided by the {@link Configuration#translationsDir()}
     *
     * @return {@link String}
     */
    String errorMessage() default "";
}
