package org.greports.utils;

import java.util.Map;

public class Translator {
    private final Map<String, Object> translations;

    private Translator(final TranslationsParser translationsParser) {
        this.translations = translationsParser.getTranslations();
    }

    public Translator(String locale, String translationDir, TranslationsParser.FileExtensions translationFileExt) {
        this(new TranslationsParser(locale, translationDir, translationFileExt));
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
