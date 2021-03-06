package org.greports.validators;

public final class BooleanValidator extends AbstractCellValidator {

    public BooleanValidator(String params) {
        super(params);
    }

    @Override
    public boolean isValid(Object object) {
        return object instanceof Boolean;
    }
}
