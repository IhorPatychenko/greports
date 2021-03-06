package org.greports.validators;

public final class FloatValidator extends AbstractCellValidator {

    public FloatValidator(String params) {
        super(params);
    }

    @Override
    public boolean isValid(Object object) {
        return object instanceof Float;
    }
}
