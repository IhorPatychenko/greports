package org.greports.utils;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.greports.exceptions.GreportsRuntimeException;
import org.greports.services.LoggerService;

import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

public class Utils {

    private static final LoggerService _logger = new LoggerService(Utils.class, true, Level.ALL);

    private Utils() {
    }

    @SafeVarargs
    public static <T> boolean anyNotNull(T... objects) {
        for (T object : objects) {
            if(!Objects.isNull(object)){
                return true;
            }
        }
        return false;
    }

    public static String capitalizeString(String str) {
        if(str != null){
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return null;
    }

    public static Locale getLocale(final String localeString) {
        return LocaleUtils.toLocale(localeString);
    }

    public static String generateId(final String idPrefix, final String id) {
        if(StringUtils.EMPTY.equals(idPrefix)) {
            return id;
        }
        return new StringJoiner("_").add(idPrefix).add(id).toString();
    }

    public static void validateNotNull(final Object object) {
        if(Objects.isNull(object)) {
            _logger.fatal("The object is null");
            throw new GreportsRuntimeException("The object is null", Utils.class);
        }
    }
}
