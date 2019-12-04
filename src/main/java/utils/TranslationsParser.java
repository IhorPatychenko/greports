package utils;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class TranslationsParser {

    public enum FileExtensions {
        YML("yml"), YAML("yaml");

        private String extension;

        FileExtensions(String fileExtension) {
            this.extension = fileExtension;
        }

        @Override
        public String toString(){
            return this.extension;
        }
    }

    private FileExtensions fileExtension = FileExtensions.YML;
    private String translationsDir;

    public TranslationsParser(String translationsDir) {
        this.translationsDir = translationsDir;
    }

    public Map<String, Object> parse(String reportLang) {
        Yaml yaml = new Yaml();
        InputStream inputStream;
        try {
            StringJoiner joiner = new StringJoiner(".");
            joiner.add(translationsDir + "messages")
                    .add(reportLang)
                    .add(fileExtension.toString());
            inputStream = new FileInputStream(joiner.toString());
            return yaml.load(inputStream);
        } catch (FileNotFoundException e) {
            return new HashMap<>();
        }
    }

    public TranslationsParser setFileExtension(FileExtensions extension) {
        this.fileExtension = extension;
        return this;
    }

}
