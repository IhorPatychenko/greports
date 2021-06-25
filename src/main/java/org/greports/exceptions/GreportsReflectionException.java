package org.greports.exceptions;

public class GreportsReflectionException extends Exception {
    private static final long serialVersionUID = 4178407471786477497L;
    private final Class<?> clazz;

    public GreportsReflectionException(final String message, final Class<?> clazz) {
        super(message);
        this.clazz = clazz;
    }

    public GreportsReflectionException(final String message, final Throwable cause, final Class<?> clazz) {
        super(message, cause);
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

}
