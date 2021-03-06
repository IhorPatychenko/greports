package org.greports.validators;

import java.util.Date;

public final class DateValidator extends AbstractCellValidator {

    public DateValidator(String params) {
        super(params);
    }

    @Override
    public boolean isValid(Object object) {
        return object instanceof Date;
    }
}
