package exceptions;

public class ReportEngineValidationException extends ReportEngineRuntimeException {

    public ReportEngineValidationException() {}

    public ReportEngineValidationException(String message, Integer exceptionCode) {
        super(message, exceptionCode);
    }
}
