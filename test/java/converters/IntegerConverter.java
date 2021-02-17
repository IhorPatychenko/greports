package converters;

import org.greports.converters.AbstractValueConverter;

public class IntegerConverter implements AbstractValueConverter {
    @Override
    public Object convert(Object input, String[] params) {
        return input;
    }

    @Override
    public Class<?> getToClass() {
        return Integer.class;
    }
}
