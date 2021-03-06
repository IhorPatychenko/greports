package org.greports.validators;

public final class ShortValidator extends AbstractCellValidator {

    public ShortValidator(String params) {
        super(params);
    }

    @Override
    public boolean isValid(Object object) {
        return object instanceof Short;
    }
}
