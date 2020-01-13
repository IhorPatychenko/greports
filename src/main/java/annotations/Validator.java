package annotations;

import validators.AbstractValidator;

public @interface Validator {
    Class<? extends AbstractValidator> validatorClass();
    String value() default "";
    String errorMessage() default "";
}
