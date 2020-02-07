package org.greports.utils;

import java.util.Map;

public class Translator {
    private final Map<String, Object> translations;

    public Translator(final Map<String, Object> translations) {
        this.translations = translations;
    }

    public String translate(String key, Object... params){
        return String.format(translations.getOrDefault(key, key).toString(), params);
    }
}
