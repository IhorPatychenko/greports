package utils;

import exceptions.ReportEngineReflectionException;
import exceptions.ReportEngineRuntimeExceptionCode;

import java.lang.reflect.Method;
import java.util.List;

public class ReflectionUtils {
    public static <T> Method getMethodWithName(Class<T> clazz, String methodName, Class<?>[] parameters) throws ReportEngineReflectionException {
        try {
            return clazz.getDeclaredMethod(methodName, parameters);
        } catch (NoSuchMethodException e) {
            throw new ReportEngineReflectionException(e.getMessage(), ReportEngineRuntimeExceptionCode.NO_METHOD_ERROR);
        }
    }
}
