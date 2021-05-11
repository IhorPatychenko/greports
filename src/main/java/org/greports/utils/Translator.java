package org.greports.utils;

import java.text.MessageFormat;
import java.util.Map;

public class Translator {
    private final Map<String, Object> translations;

    private Translator(final TranslationsParser translationsParser) {
        this.translations = translationsParser.getTranslations();
    }

    public Translator(String locale, String translationDir, TranslationsParser.FileExtensions translationFileExt) {
        this(new TranslationsParser(locale, translationDir, translationFileExt));
    }

    public String translate(String key, Object... params){
        if(key != null) {
            return MessageFormat.format(translations.getOrDefault(key, key).toString(), params);
        }
        return null;
    }
}
