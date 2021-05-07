package org.greports.utils;

import org.greports.engine.ReportConfiguration;

import java.util.Map;

public class Translator {
    private final Map<String, Object> translations;

    private Translator(final TranslationsParser translationsParser) {
        this.translations = translationsParser.getTranslations();
    }

    public Translator(ReportConfiguration config) {
        this(new TranslationsParser(config.getLocale(), config.getTranslationsDir(), config.getTranslationFileExtension()));
    }

    public String translate(String key, String... params){
        if(key != null) {
            String text = translations.getOrDefault(key, key).toString();
            for (int i = 0; i < params.length; i++) {
                text = text.replaceAll("\\{" + i + "}", params[i]);
            }
            return text;
        }
        return null;
    }
}
