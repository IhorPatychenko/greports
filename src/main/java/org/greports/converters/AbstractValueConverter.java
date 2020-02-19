package org.greports.converters;

public abstract class AbstractValueConverter {
    public abstract Object convert(final Object input, final String[] params);
    public abstract Class getToClass();
}
