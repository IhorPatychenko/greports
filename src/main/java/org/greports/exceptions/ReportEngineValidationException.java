package org.greports.exceptions;

public class ReportEngineValidationException extends ReportEngineRuntimeException {

    private static final long serialVersionUID = 8301897426207542805L;

    private Integer rowIndex;

    public ReportEngineValidationException(final String message, final Class<?> clazz) {
        super(message, clazz);
    }

    public ReportEngineValidationException(final String message, final Class<?> clazz, final Integer rowIndex) {
        super(message, clazz);
        this.rowIndex = rowIndex;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }
}
