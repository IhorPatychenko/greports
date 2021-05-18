package converters;

import org.greports.converters.AbstractValueConverter;

import java.util.Date;

public class InvalidDateConverter implements AbstractValueConverter {
    @Override
    public Object convert(Object input, String[] params) {
        if(input == null || input instanceof String) {
            return null;
        }
        return input;
    }

    @Override
    public Class<?> getToClass() {
        return Date.class;
    }
}
