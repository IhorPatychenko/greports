package exceptions;

public class ReportEngineValidationException extends ReportEngineRuntimeException {

    private Integer rowIndex;

    public ReportEngineValidationException(String message, Integer exceptionCode) {
        super(message, exceptionCode);
    }

    public ReportEngineValidationException(String message, Integer exceptionCode, Integer rowIndex) {
        super(message, exceptionCode);
        this.rowIndex = rowIndex;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }
}
