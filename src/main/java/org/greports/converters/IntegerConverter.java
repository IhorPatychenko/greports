package org.greports.converters;

public class IntegerConverter extends AbstractValueConverter {

    @Override
    public Integer convert(final Object input, final String[] params) {
        return Integer.parseInt(String.valueOf(input));
    }

    @Override
    public Class getToClass() {
        return Integer.class;
    }
}
