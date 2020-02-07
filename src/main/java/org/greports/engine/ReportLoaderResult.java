package org.greports.engine;

import org.apache.poi.ss.usermodel.Cell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportLoaderResult implements Serializable {

    private static final long serialVersionUID = 9015684910370931236L;

    private final Map<Class<?>, List<?>> results = new HashMap<>();
    private final Map<Class<?>, List<ReportLoaderError>> errors = new HashMap<>();

    protected <T> void addResult(Class<T> clazz, List<T> list) {
        results.put(clazz, list);
    }

    protected <T> void addError(Class<T> clazz, Cell cell, String columnTitle, String errorMessage) {
        errorsCheckClass(clazz);
        errors.get(clazz).add(new ReportLoaderError(cell, columnTitle, errorMessage));
    }

    protected <T> void addError(Class<T> clazz, String sheetName, Integer rowIndex, Integer columnIndex, String columnTitle, String errorMessage) {
        errorsCheckClass(clazz);
        errors.get(clazz).add(new ReportLoaderError(sheetName, rowIndex, columnIndex, columnTitle, errorMessage));
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getResult(Class<T> clazz) {
        return ((List<T>) results.getOrDefault(clazz, new ArrayList<T>()));
    }

    public <T> List<ReportLoaderError> getErrors(Class<T> clazz) {
        errorsCheckClass(clazz);
        return errors.get(clazz);
    }

    public <T> List<ReportLoaderError> getErrors(Class<T> clazz, int limit) {
        List<ReportLoaderError> list = new ArrayList<>();
        if (limit > 0) {
            list = getErrors(clazz).stream().limit(limit).collect(Collectors.toList());
        }
        return list;
    }

    private <T> void errorsCheckClass(Class<T> clazz) {
        if (!errors.containsKey(clazz)) {
            errors.put(clazz, new ArrayList<>());
        }
    }

    public <T> Map<Class<?>, List<ReportLoaderError>> getErrors() {
        return Collections.unmodifiableMap(errors);
    }

    public <T> Map<Class<?>, List<ReportLoaderError>> getErrors(int limit) {
        Map<Class<?>, List<ReportLoaderError>> map = new HashMap<>();
        if (limit > 1) {
            for (final Map.Entry<Class<?>, List<ReportLoaderError>> entry : errors.entrySet()) {
                map.put(entry.getKey(), getErrors(entry.getKey(), limit));
            }
        }
        return map;
    }

    public boolean hasErrors() {
        for (final Map.Entry<Class<?>, List<ReportLoaderError>> entry : errors.entrySet()) {
            if (entry.getValue().size() > 0) {
                return true;
            }
        }
        return false;
    }

    public <T> boolean hasErrors(Class<T> clazz) {
        return errors.containsKey(clazz);
    }
}
