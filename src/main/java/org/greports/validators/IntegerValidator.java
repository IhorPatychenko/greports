package org.greports.validators;

public final class IntegerValidator extends AbstractCellValidator {

    public IntegerValidator(String params) {
        super(params);
    }

    @Override
    public boolean isValid(Object object) {
        return object instanceof Integer;
    }
}
