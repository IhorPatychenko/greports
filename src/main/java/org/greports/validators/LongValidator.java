package org.greports.validators;

public final class LongValidator extends AbstractCellValidator {

    public LongValidator(String params) {
        super(params);
    }

    @Override
    public boolean isValid(Object object) {
        return object instanceof Long;
    }
}
