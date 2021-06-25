package org.greports.exceptions;

public class GreportsRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 866503321250326755L;

    private Class<?> clazz;

    public GreportsRuntimeException(final String message, final Class<?> clazz) {
        super(message);
        this.clazz = clazz;
    }

    public GreportsRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public GreportsRuntimeException(final String message, final Throwable cause, final Class<?> clazz) {
        super(message, cause);
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
