package org.greports.utils;

import org.greports.exceptions.ReportEngineReflectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReflectionUtils {

    private static final List<String> gettersPrefixes = new ArrayList<>(Arrays.asList("get", "is"));
    private static final List<String> settersPrefixes = new ArrayList<>(Collections.singletonList("set"));

    public static <T> Method getMethodWithName(Class<T> clazz, String methodName, Class<?>[] parameters) throws ReportEngineReflectionException {
        try {
            return clazz.getDeclaredMethod(methodName, parameters);
        } catch (NoSuchMethodException e) {
            if(clazz.getSuperclass() != null){
                return getMethodWithName(clazz.getSuperclass(), methodName, parameters);
            }
            throw new ReportEngineReflectionException(e.getMessage(), e, clazz);
        }
    }

    public static <T> T newInstance(Class<T> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        return declaredConstructor.newInstance();
    }

    public static <T> Method fetchFieldGetter(Field field, Class<T> clazz) throws ReportEngineReflectionException {
        List<String> getterPossibleNames = new ArrayList<>();
        gettersPrefixes.forEach(prefix -> getterPossibleNames.addAll(Arrays.asList(prefix + Utils.capitalizeString(field.getName()), prefix + field.getName())));

        for (String getterPossibleName : getterPossibleNames) {
            try {
                final Method method = ReflectionUtils.getMethodWithName(clazz, getterPossibleName, new Class<?>[]{});
                if (method != null) {
                    return method;
                }
            } catch (ReportEngineReflectionException ignored) {}
        }
        throw new ReportEngineReflectionException(
            "No getter was found with any of these names \"" + String.join(", ", getterPossibleNames) + "\" for field " + field.getName() + " in class @" + clazz.getSimpleName(),
            clazz
        );
    }

    public static <T> Method fetchFieldSetter(Field field, Class<T> clazz) throws ReportEngineReflectionException {
        List<String> setterPossibleNames = new ArrayList<>();
        settersPrefixes.forEach(prefix -> setterPossibleNames.addAll(Arrays.asList(prefix + Utils.capitalizeString(field.getName()), prefix + field.getName())));

        for (String setterPossibleName : setterPossibleNames) {
            try {
                Class<?>[] returnType = { field.getType() };
                final Method method = ReflectionUtils.getMethodWithName(clazz, setterPossibleName, returnType);
                if (method != null) {
                    return method;
                }
            } catch (ReportEngineReflectionException ignored) {}
        }
        throw new ReportEngineReflectionException(
                "No setter was found with any of these names \"" + String.join(", ", setterPossibleNames) + "\" for field " + field.getName(),
                clazz
        );
    }
}
