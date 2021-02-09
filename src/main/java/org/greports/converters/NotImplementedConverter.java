package org.greports.converters;

public class NotImplementedConverter implements AbstractValueConverter {
    @Override
    public Object convert(Object input, String[] params) {
        return input;
    }

    @Override
    public Class<?> getToClass() {
        return Object.class;
    }
}
