package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportLoaderResult {

    private Map<Class<?>, List<?>> results = new HashMap<>();
    private Map<Class<?>, List<ReportLoaderError>> errors = new HashMap<>();

    protected <T> void addResult(Class<T> clazz, List<T> list){
        results.put(clazz, list);
    }

    public <T> List<T> getResult(Class<T> clazz) {
        return ((List<T>) results.get(clazz));
    }

    protected <T> void addError(Class<T> clazz, ReportLoaderError error) {
        if(!errors.containsKey(clazz)){
            errors.put(clazz, new ArrayList<>());
        }
        errors.get(clazz).add(error);
    }

    public <T> List<ReportLoaderError> getErrors(Class<T> clazz) {
        if(!errors.containsKey(clazz)){
            errors.put(clazz, new ArrayList<>());
        }
        return errors.get(clazz);
    }

}
