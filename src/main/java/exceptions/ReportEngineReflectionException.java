package exceptions;

public class ReportEngineReflectionException extends ReportEngineRuntimeException {
    private static final long serialVersionUID = 4178407471786477497L;
    private final Class<?> clazz;
    private final Object target;

    public ReportEngineReflectionException(final String message, final Integer exceptionCode, final Class<?> clazz, final Object target) {
        super(message, exceptionCode);
        this.clazz = clazz;
        this.target = target;
    }

    public ReportEngineReflectionException(final String message, final Integer exceptionCode, final StackTraceElement[] stackTraceElements, final Class<?> clazz, final Object target) {
        super(message, exceptionCode, stackTraceElements);
        this.clazz = clazz;
        this.target = target;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object getTarget() {
        return target;
    }
}
