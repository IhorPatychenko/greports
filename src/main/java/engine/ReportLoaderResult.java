package engine;

import org.apache.poi.ss.usermodel.Cell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportLoaderResult implements Serializable {

    private static final long serialVersionUID = 9015684910370931236L;

    private Map<Class<?>, List<?>> results = new HashMap<>();
    private Map<Class<?>, List<ReportLoaderError>> errors = new HashMap<>();

    protected <T> void addResult(Class<T> clazz, List<T> list){
        results.put(clazz, list);
    }

//    protected <T> void addError(Class<T> clazz, ReportLoaderError error) {
//        if(!errors.containsKey(clazz)){
//            errors.put(clazz, new ArrayList<>());
//        }
//        errors.get(clazz).add(error);
//    }

    protected <T> void addError(Class<T> clazz, Cell cell, String errorMessage, String columnTitle){
        if(!errors.containsKey(clazz)){
            errors.put(clazz, new ArrayList<>());
        }
        errors.get(clazz).add(new ReportLoaderError(cell, errorMessage, columnTitle));
    }

    public <T> List<T> getResult(Class<T> clazz) {
        return ((List<T>) results.getOrDefault(clazz, new ArrayList<T>()));
    }

    public <T> List<ReportLoaderError> getErrors(Class<T> clazz) {
        if(!errors.containsKey(clazz)){
            errors.put(clazz, new ArrayList<>());
        }
        return errors.get(clazz);
    }

    public <T> Map<Class<?>, List<ReportLoaderError>> getErrors(){
        return Collections.unmodifiableMap(errors);
    }

    public boolean hasErrors(){
        for (final Map.Entry<Class<?>, List<ReportLoaderError>> entry : errors.entrySet()) {
            if(entry.getValue().size() > 0){
                return true;
            }
        }
        return false;
    }

    public <T> boolean hasErrors(Class<T> clazz) {
        return errors.containsKey(clazz);
    }
}
