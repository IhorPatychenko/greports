package exceptions;

public abstract class ReportEngineRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -7216101554733328926L;
    private Integer exceptionCode;
    private StackTraceElement[] stackTraceElements = new StackTraceElement[]{};

    public ReportEngineRuntimeException() {}

    public ReportEngineRuntimeException(final String message, final Integer exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public ReportEngineRuntimeException(final String message, final Integer exceptionCode, final StackTraceElement[] stackTraceElements) {
        super(message);
        this.exceptionCode = exceptionCode;
        this.stackTraceElements = stackTraceElements;
    }

    public Integer getExceptionCode() {
        return exceptionCode;
    }

    public StackTraceElement[] getStackTraceElements() {
        return stackTraceElements;
    }
}
