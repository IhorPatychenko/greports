package org.greports.validators;

public abstract class AbstractCellValidator extends AbstractValidator {

    protected AbstractCellValidator(final String params) {
        super(params);
    }

    public abstract boolean isValid(final Object object);
}
