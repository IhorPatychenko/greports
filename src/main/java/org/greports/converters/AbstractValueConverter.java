package org.greports.converters;

public interface AbstractValueConverter {
    Object convert(final Object input, final String[] params);
    @Deprecated
    Class<?> getToClass();
}
