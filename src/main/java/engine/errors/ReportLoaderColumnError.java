package engine.errors;

public class ReportLoaderColumnError {
    private final String errorMessage;

    public ReportLoaderColumnError(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
