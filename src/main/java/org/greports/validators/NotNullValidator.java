package org.greports.validators;

import java.util.Objects;

public final class NotNullValidator extends AbstractCellValidator {

    public NotNullValidator(String params) {
        super(params);
    }

    @Override
    public boolean isValid(Object object) {
        return !Objects.isNull(object);
    }
}
