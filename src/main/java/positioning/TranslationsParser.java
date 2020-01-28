package positioning;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
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

    private FileExtensions fileExtension = FileExtensions.YML;
    private final String translationsDir;

    public TranslationsParser(String translationsDir) {
        this.translationsDir = translationsDir;
    }

    public Map<String, Object> parse(String reportLang) throws IOException {
        Yaml yaml = new Yaml();
        InputStream inputStream;
        final URL resource = getClass().getClassLoader().getResource(String.format("%smessages.%s.%s", translationsDir, reportLang, fileExtension.toString()));
        if(resource != null){
            inputStream = resource.openStream();
            return yaml.load(inputStream);
        } else {
            return new HashMap<>();
        }
    }

    public TranslationsParser setFileExtension(FileExtensions extension) {
        this.fileExtension = extension;
        return this;
    }

}
