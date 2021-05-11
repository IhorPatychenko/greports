package org.greports.utils;

import org.greports.engine.ReportConfiguration;

import java.text.MessageFormat;
import java.util.Map;

public class Translator {
    private final Map<String, Object> translations;

    private Translator(final TranslationsParser translationsParser) {
        this.translations = translationsParser.getTranslations();
    }

    public Translator(ReportConfiguration config) {
        this(new TranslationsParser(config.getLocale(), config.getTranslationsDir(), config.getTranslationFileExtension()));
    }

    public String translate(String key, Object... params){
        if(key != null) {
            return MessageFormat.format(translations.getOrDefault(key, key).toString(), params);
        }
        return null;
    }
}
