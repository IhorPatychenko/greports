package utils;

import exceptions.ReportEngineReflectionException;

import java.lang.reflect.Method;

public class ReflectionUtils {
    public static <T> Method getMethodWithName(Class<T> clazz, String value) throws ReportEngineReflectionException {
        try {
            return clazz.getDeclaredMethod(value);
        } catch (NoSuchMethodException e) {
            throw new ReportEngineReflectionException(e.getMessage());
        }
    }
}
