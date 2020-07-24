package org.greports.converters;

public class StringConverter implements AbstractValueConverter {

    @Override
    public String convert(final Object input, final String[] params) {
        return String.valueOf(input);
    }

    @Override
    public Class getToClass() {
        return String.class;
    }
}
