package utils;

import exceptions.ReportEngineReflectionException;
import exceptions.ReportEngineRuntimeExceptionCode;

import java.lang.reflect.Method;

public class ReflectionUtils {
    public static <T> Method getMethodWithName(Class<T> clazz, String methodName) throws ReportEngineReflectionException {
        try {
            return clazz.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new ReportEngineReflectionException(e.getMessage(), ReportEngineRuntimeExceptionCode.NO_METHOD_ERROR);
        }
    }
}
