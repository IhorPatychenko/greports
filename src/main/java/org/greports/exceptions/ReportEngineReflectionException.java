package org.greports.exceptions;

public class ReportEngineReflectionException extends Exception {
    private static final long serialVersionUID = 4178407471786477497L;
    private final Class<?> clazz;

    public ReportEngineReflectionException(final String message, final Class<?> clazz) {
        super(message);
        this.clazz = clazz;
    }

    public ReportEngineReflectionException(final String message, final Throwable cause, final Class<?> clazz) {
        super(message, cause);
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

}
