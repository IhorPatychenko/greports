package org.greports.validators;

public final class DoubleValidator extends AbstractCellValidator {

    public DoubleValidator(String params) {
        super(params);
    }

    @Override
    public boolean isValid(Object object) {
        return object instanceof Double;
    }
}
