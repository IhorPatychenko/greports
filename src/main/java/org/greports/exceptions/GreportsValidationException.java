package org.greports.exceptions;

import java.io.Serializable;

public class GreportsValidationException extends GreportsRuntimeException {

    private static final long serialVersionUID = 8301897426207542805L;

    private Integer rowIndex;
    private final Serializable errorValue;

    public GreportsValidationException(final String message, final Class<?> clazz) {
        this(message, clazz, null, null);
    }

    public GreportsValidationException(final String message, final Class<?> clazz, final Integer rowIndex, final Serializable errorValue) {
        super(message, clazz);
        this.rowIndex = rowIndex;
        this.errorValue = errorValue;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public Object getErrorValue() {
        return errorValue;
    }
}
