package org.greports.utils;

import org.apache.commons.lang3.reflect.MethodUtils;
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

    private ReflectionUtils() {}

    public static <T> T newInstance(Class<T> clazz) throws ReportEngineReflectionException {
        try {
            final Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            return declaredConstructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ReportEngineReflectionException(String.format(ErrorMessages.SHOULD_HAVE_EMPTY_CONSTRUCTOR, clazz), e, clazz);
        }
    }

    public static <T> Method fetchFieldGetter(Field field, Class<T> clazz) throws ReportEngineReflectionException {
        List<String> getterPossibleNames = generateMethodNames(field, gettersPrefixes);
        for (String getterPossibleName : getterPossibleNames) {
            final Method method = MethodUtils.getMatchingMethod(clazz, getterPossibleName);
            if (method != null) {
                return method;
            }
        }
        throw new ReportEngineReflectionException(
            String.format("No getter was found with any of these names \"%s\" for field %s in class %s", String.join(", ", getterPossibleNames), field.getName(), clazz.getName()),
            clazz
        );
    }

    public static <T> Method fetchFieldSetter(Field field, Class<T> clazz) throws ReportEngineReflectionException {
        List<String> setterPossibleNames = generateMethodNames(field, settersPrefixes);
        for (String setterPossibleName : setterPossibleNames) {
            final Method method = MethodUtils.getMatchingMethod(clazz, setterPossibleName);
            if (method != null) {
                return method;
            }
        }
        throw new ReportEngineReflectionException(
            String.format("No setter was found with any of these names \"%s\" for field %s in class %s", String.join(", ", setterPossibleNames), field.getName(), clazz.getName()),
            clazz
        );
    }

    private static List<String> generateMethodNames(Field field, List<String> prefixes) {
        List<String> possibleNames = new ArrayList<>();
        prefixes.forEach(prefix -> {
            possibleNames.add(prefix + Utils.capitalizeString(field.getName()));
            possibleNames.add(prefix + field.getName());
        });
        return possibleNames;
    }

    public static Object invokeMethod(Method method, Object object) throws ReportEngineReflectionException {
        try {
            return method.invoke(object);
        } catch (IllegalAccessException e) {
            throw new ReportEngineReflectionException(ErrorMessages.INV_METHOD_WITH_NO_ACCESS, e, ReflectionUtils.class);
        } catch (InvocationTargetException e) {
            throw new ReportEngineReflectionException(ErrorMessages.INV_METHOD, e, ReflectionUtils.class);
        }
    }

    public static boolean isListOrArray(Class<?> clazz){
        return clazz != null && (clazz.isArray() || clazz.equals(List.class));
    }
}
