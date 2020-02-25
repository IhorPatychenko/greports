package org.greports.exceptions;

public class ReportEngineRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 866503321250326755L;

    private Class<?> clazz;

    public ReportEngineRuntimeException(final String message, final Class<?> clazz) {
        super(message);
        this.clazz = clazz;
    }

    public ReportEngineRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ReportEngineRuntimeException(final String message, final Throwable cause, final Class<?> clazz) {
        super(message, cause);
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
