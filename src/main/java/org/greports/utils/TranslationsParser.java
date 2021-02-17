package org.greports.utils;

import org.greports.exceptions.ReportEngineRuntimeException;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TranslationsParser {

    public enum FileExtensions {
        YML("yml"), YAML("yaml");

        private final String extension;

        FileExtensions(String fileExtension) {
            this.extension = fileExtension;
        }

        @Override
        public String toString(){
            return this.extension;
        }
    }

    private final String locale;
    private final String translationsDir;
    private FileExtensions fileExtension;

    public TranslationsParser(String locale, String translationsDir, FileExtensions fileExtension) {
        this.locale = locale;
        this.translationsDir = translationsDir;
        this.fileExtension = fileExtension;
    }

    public Map<String, Object> getTranslations() {
        return parse(Utils.getLocale(locale));
    }

    public Map<String, Object> parse(Locale locale) {
        Yaml yaml = new Yaml();
        InputStream inputStream;
        final String fileURL = String.format("%smessages.%s.%s", translationsDir, locale.getLanguage(), fileExtension.toString());
        final URL resource = getClass().getClassLoader().getResource(fileURL);
        try {
            if(resource != null){
                inputStream = resource.openStream();
                return yaml.load(inputStream);
            } else {
                return new HashMap<>();
            }
        } catch (IOException e) {
            throw new ReportEngineRuntimeException(String.format("Error parsing translations file %s", fileURL), e);
        }
    }

    public TranslationsParser setFileExtension(FileExtensions extension) {
        this.fileExtension = extension;
        return this;
    }

}
