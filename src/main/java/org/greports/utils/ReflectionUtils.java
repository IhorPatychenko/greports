package org.greports.utils;

import org.greports.exceptions.GreportsReflectionException;

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

    public static <T> T newInstance(Class<T> clazz, Object... args) throws GreportsReflectionException {
        try {
            final Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            return declaredConstructor.newInstance(args);
        } catch (ReflectiveOperationException e) {
            throw new GreportsReflectionException(String.format(ErrorMessages.SHOULD_HAVE_EMPTY_CONSTRUCTOR, clazz), e, clazz);
        }
    }

    public static <T> Method getMethodWithName(String methodName, Class<T> clazz, Class<?>... parameters) {
        try {
            return clazz.getDeclaredMethod(methodName, parameters);
        } catch (NoSuchMethodException e) {
            if(clazz.getSuperclass() != null){
                return getMethodWithName(methodName, clazz.getSuperclass(), parameters);
            }
            return null;
        }
    }

    public static <T> Method fetchFieldGetter(String fieldName, Class<T> clazz) throws GreportsReflectionException {
        List<String> getterPossibleNames = generateMethodNames(fieldName, gettersPrefixes);
        return catchFieldMethod(getterPossibleNames, clazz, fieldName);
    }

    public static <T> Method fetchFieldGetter(Field field, Class<T> clazz) throws GreportsReflectionException {
        List<String> getterPossibleNames = generateMethodNames(field, gettersPrefixes);
        return catchFieldMethod(getterPossibleNames, clazz, field.getName());
    }

    private static <T> Method catchFieldMethod(List<String> getterPossibleNames, Class<T> clazz, String fieldName) throws GreportsReflectionException {
        for (String getterPossibleName : getterPossibleNames) {
            final Method method = getMethodWithName(getterPossibleName, clazz);
            if (method != null) {
                return method;
            }
        }
        throw new GreportsReflectionException(
                String.format("No getter was found with any of these names \"%s\" for field %s in class %s", String.join(", ", getterPossibleNames), fieldName, clazz.getName()),
                clazz
        );
    }

    public static <T> Method fetchFieldSetter(Field field, Class<T> clazz) throws GreportsReflectionException {
        List<String> setterPossibleNames = generateMethodNames(field, settersPrefixes);
        for (String setterPossibleName : setterPossibleNames) {
            final Method method = getMethodWithName(setterPossibleName, clazz, field.getType());
            if (method != null) {
                return method;
            }
        }
        throw new GreportsReflectionException(
            String.format("No setter was found with any of these names \"%s\" for field %s in class %s", String.join(", ", setterPossibleNames), field.getName(), clazz.getName()),
            clazz
        );
    }

    private static List<String> generateMethodNames(Field field, List<String> prefixes) {
        return generateMethodNames(field.getName(), prefixes);
    }

    private static List<String> generateMethodNames(String fieldName, List<String> prefixes) {
        List<String> possibleNames = new ArrayList<>();
        prefixes.forEach(prefix -> {
            possibleNames.add(prefix + Utils.capitalizeString(fieldName));
            possibleNames.add(prefix + fieldName);
        });
        return possibleNames;
    }

    public static Object invokeMethod(Method method, Object object) throws GreportsReflectionException {
        try {
            return method.invoke(object);
        } catch (IllegalAccessException e) {
            throw new GreportsReflectionException(ErrorMessages.INV_METHOD_WITH_NO_ACCESS, e, ReflectionUtils.class);
        } catch (InvocationTargetException e) {
            throw new GreportsReflectionException(ErrorMessages.INV_METHOD, e, ReflectionUtils.class);
        }
    }

    public static boolean isListOrArray(Class<?> clazz){
        return clazz != null && (clazz.isArray() || clazz.equals(List.class));
    }
}
