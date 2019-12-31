package exceptions;

public class ReportEngineReflectionException extends ReportEngineRuntimeException {
    public ReportEngineReflectionException(final String message, final Integer exceptionCode) {
        super(message, exceptionCode);
    }
}
