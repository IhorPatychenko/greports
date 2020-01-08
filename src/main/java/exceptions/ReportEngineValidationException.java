package exceptions;

public class ReportEngineValidationException extends ReportEngineRuntimeException {
    public ReportEngineValidationException(String message, Integer exceptionCode) {
        super(message, exceptionCode);
    }
}
