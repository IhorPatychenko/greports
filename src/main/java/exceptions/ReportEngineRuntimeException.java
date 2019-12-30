package exceptions;

public abstract class ReportEngineRuntimeException extends RuntimeException {
    public ReportEngineRuntimeException(String message) {
        super(message);
    }
}
