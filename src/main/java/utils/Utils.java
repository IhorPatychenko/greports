package utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static utils.Locales.*;

public class Utils {

    private static final Map<Locales, Locale> _localesMap = new HashMap<Locales, Locale>(){{
        put(ENGLISH, Locale.ENGLISH);
        put(FRENCH, Locale.FRENCH);
        put(GERMAN, Locale.GERMAN);
        put(SPANISH, new Locale("es", ""));
        put(ITALIAN, Locale.ITALIAN);
        put(JAPANESE, Locale.JAPANESE);
        put(KOREAN, Locale.KOREAN);
        put(CHINESE, Locale.CHINESE);
        put(SIMPLIFIED_CHINESE, Locale.SIMPLIFIED_CHINESE);
        put(TRADITIONAL_CHINESE, Locale.TRADITIONAL_CHINESE);
        put(FRANCE, Locale.FRANCE);
        put(GERMANY, Locale.GERMANY);
        put(SPAIN, new Locale("es", "ES"));
        put(ITALY, Locale.ITALY);
        put(JAPAN, Locale.JAPAN);
        put(KOREA, Locale.KOREA);
        put(CHINA, Locale.CHINA);
        put(PRC, Locale.PRC);
        put(TAIWAN, Locale.TAIWAN);
        put(UK, Locale.UK);
        put(US, Locale.US);
        put(CANADA, Locale.CANADA);
        put(CANADA_FRENCH, Locale.CANADA_FRENCH);
    }};

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

    public static Locale getLocale(Locales locale) {
        return _localesMap.get(locale);
    }
}
