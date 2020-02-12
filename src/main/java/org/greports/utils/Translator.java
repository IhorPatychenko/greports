package org.greports.utils;

import java.util.Map;

public class Translator {
    private final Map<String, Object> translations;

    public Translator(final Map<String, Object> translations) {
        this.translations = translations;
    }

    public String translate(String key, String... params){
        String text = translations.getOrDefault(key, key).toString();
        for (int i = 0; i < params.length; i++) {
            text = text.replaceAll("\\{" + i + "}", params[i]);
        }
        return text;
    }
}
