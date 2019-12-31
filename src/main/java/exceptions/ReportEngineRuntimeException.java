package exceptions;

public abstract class ReportEngineRuntimeException extends RuntimeException {

    private Integer exceptionCode;

    public ReportEngineRuntimeException(String message, Integer exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public Integer getExceptionCode() {
        return exceptionCode;
    }
}
