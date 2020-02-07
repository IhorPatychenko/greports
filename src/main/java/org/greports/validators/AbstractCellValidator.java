package org.greports.validators;

public abstract class AbstractCellValidator extends AbstractValidator {

    protected AbstractCellValidator(final String validatorValue) {
        super(validatorValue);
    }

    @Override
    public boolean isValid(final Object object) {
        return false;
    }
}
