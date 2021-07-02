package org.greports.converters;

public final class NotImplementedConverter implements AbstractValueConverter {
    @Override
    public Object convert(Object input, String... params) {
        return input;
    }

    @Override
    public Class<?> getToClass() {
        return Object.class;
    }
}
