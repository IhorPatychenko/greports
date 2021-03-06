package org.greports.validators;

import java.util.List;

public abstract class AbstractColumnValidator extends AbstractValidator {

    protected AbstractColumnValidator(final String validatorValue) {
        super(validatorValue);
    }

    public abstract int getErrorRowIndex(final List<Object> list);

    public abstract boolean isValid(final List<Object> object);

    public abstract Object getErrorValue();
}
