package org.greports.utils;

import org.greports.annotations.Converter;
import org.greports.converters.AbstractValueConverter;
import org.greports.exceptions.ReportEngineReflectionException;

import java.util.List;

/**
 * Converter utils class. This one is for internal use of greports engine.
 */
public class ConverterUtils {

    private ConverterUtils() {}

    public static Object convertValue(final Object value, final Converter converter) throws ReportEngineReflectionException {
        final Class<? extends AbstractValueConverter> clazz = converter.converterClass();
        final AbstractValueConverter valueConverter = ReflectionUtils.newInstance(clazz);
        return valueConverter.convert(value, converter.params());
    }
}
