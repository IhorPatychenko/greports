package org.greports.validators;

public final class NumberValidator extends AbstractCellValidator {

    public NumberValidator(String params) {
        super(params);
    }

    @Override
    public boolean isValid(Object object) {
        return object instanceof Number;
    }
}
