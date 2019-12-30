package utils;

import java.lang.reflect.Method;

public class ReflectionUtils {
    public static <T> Method getMethodWithName(Class<T> clazz, String value) throws NoSuchMethodException {
        return clazz.getDeclaredMethod(value);
    }
}
