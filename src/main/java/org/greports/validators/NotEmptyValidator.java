package org.greports.validators;


import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class NotEmptyValidator extends AbstractCellValidator {

    protected NotEmptyValidator(String params) {
        super(params);
    }

    @Override
    public boolean isValid(Object object) {
        return object != null && StringUtils.isNotEmpty(Objects.toString(object));
    }
}
