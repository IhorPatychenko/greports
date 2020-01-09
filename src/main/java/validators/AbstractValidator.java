package validators;

public abstract class AbstractValidator {

    protected String value;

    public AbstractValidator(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public abstract boolean isValid(Object object);
    public abstract String getDefaultErrorMessage();
    public abstract String getErrorKey();
}
