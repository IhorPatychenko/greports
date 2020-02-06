package exceptions;

public class ReportEngineInjectorException extends ReportEngineRuntimeException {

    private static final long serialVersionUID = -6568460464128402485L;

    public ReportEngineInjectorException(final String message, final Integer exceptionCode) {
        super(message, exceptionCode);
    }

}
