package exceptions;

public class ReportEngineIllegalArgumentException extends ReportEngineRuntimeException {

    private Class<?> valueClass;
    private Class<?> methodParameterType;

    public ReportEngineIllegalArgumentException(final String message, final Integer exceptionCode, Class<?> valueClass, Class<?> methodParameterType) {
        super(message, exceptionCode);
        this.valueClass = valueClass;
        this.methodParameterType = methodParameterType;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    public Class<?> getMethodParameterType() {
        return methodParameterType;
    }
}
