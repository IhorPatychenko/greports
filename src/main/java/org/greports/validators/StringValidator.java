package org.greports.validators;

public final class StringValidator extends AbstractCellValidator {

    public StringValidator(String params) {
        super(params);
    }

    @Override
    public boolean isValid(Object object) {
        return object instanceof String;
    }
}
