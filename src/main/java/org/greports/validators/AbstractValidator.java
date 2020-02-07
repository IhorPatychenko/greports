package org.greports.validators;

public abstract class AbstractValidator {

    protected final String validatorValue;

    protected AbstractValidator(final String validatorValue) {
        this.validatorValue = validatorValue;
    }

    public String getValidatorValue() {
        return validatorValue;
    }

    public abstract boolean isValid(Object object);
}
