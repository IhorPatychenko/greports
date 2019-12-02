package utils;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class YamlParser {

    public Map<String, Object> parse(String translationsDir, String reportLang) {
        return parse(translationsDir + "messages." + reportLang + ".yml");
    }

    public Map<String, Object> parse(String resource) {
        Yaml yaml = new Yaml();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(resource);
            return yaml.load(inputStream);
        } catch (FileNotFoundException e) {
            return new HashMap<>();
        }
    }

}
