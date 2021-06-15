package org.greports.engine;

import org.apache.poi.ss.usermodel.Cell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LoaderResult implements Serializable {

    private static final long serialVersionUID = 9015684910370931236L;

    private final Map<Class<?>, List<?>> results = new HashMap<>();
    private final Map<Class<?>, List<LoaderError>> errors = new HashMap<>();
    private final Map<Class<?>, Set<Object>> rowsWithErrors = new HashMap<>();

    protected <T> void addResult(Class<T> clazz, List<T> list) {
        results.put(clazz, list);
    }

    protected <T> void addError(Class<T> clazz, T rowWithError, Cell cell, String columnTitle, String errorMessage, final Serializable errorValue) {
        addError(clazz, rowWithError, new LoaderError(cell, columnTitle, errorMessage, errorValue));
    }

    protected <T> void addError(Class<T> clazz, String sheetName, Integer rowIndex, Integer columnIndex, String columnTitle, String errorMessage, final Serializable errorValue) {
        addError(clazz, null, new LoaderError(sheetName, rowIndex, columnIndex, columnTitle, errorMessage, errorValue));
    }

    private <T> void addError(Class<T> clazz, T rowWithError, LoaderError error) {
        errorsCheckClass(clazz);
        errors.get(clazz).add(error);
        rowsWithErrorsCheckClass(clazz);
        if(rowWithError != null) {
            rowsWithErrors.get(clazz).add(rowWithError);
        }
    }

    private <T> void rowsWithErrorsCheckClass(Class<T> clazz) {
        if (!rowsWithErrors.containsKey(clazz)) {
            rowsWithErrors.put(clazz, new HashSet<>());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getResult(Class<T> clazz) {
        return ((List<T>) results.getOrDefault(clazz, new ArrayList<>()));
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getResultWithoutErrors(Class<T> clazz) {
        final List<T> objects = results.getOrDefault(clazz, new ArrayList<>()).stream().map(e -> (T) e).collect(Collectors.toList());
        objects.removeAll(getResultWithErrors(clazz));
        return objects;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getResultWithErrors(Class<T> clazz) {
        return rowsWithErrors.getOrDefault(clazz, new HashSet<>()).stream().map(e -> (T) e).collect(Collectors.toList());
    }

    public <T> List<LoaderError> getErrors(Class<T> clazz) {
        errorsCheckClass(clazz);
        return errors.get(clazz);
    }

    public <T> List<LoaderError> getErrors(Class<T> clazz, int limit) {
        List<LoaderError> list = new ArrayList<>();
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

    public Map<Class<?>, List<LoaderError>> getErrors() {
        return Collections.unmodifiableMap(errors);
    }

    public Map<Class<?>, List<LoaderError>> getErrors(int limit) {
        Map<Class<?>, List<LoaderError>> map = new HashMap<>();
        if (limit > 1) {
            for (final Map.Entry<Class<?>, List<LoaderError>> entry : errors.entrySet()) {
                map.put(entry.getKey(), getErrors(entry.getKey(), limit));
            }
        }
        return map;
    }

    public boolean hasErrors() {
        for (final Map.Entry<Class<?>, List<LoaderError>> entry : errors.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public <T> boolean hasErrors(Class<T> clazz) {
        return errors.containsKey(clazz);
    }
}
