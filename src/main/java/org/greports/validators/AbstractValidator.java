package org.greports.validators;

public abstract class AbstractValidator {

    protected final String params;

    protected AbstractValidator(final String params) {
        this.params = params;
    }

    public String getParams() {
        return params;
    }

}
