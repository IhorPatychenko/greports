package validators;

public abstract class AbstractColumnValidator extends AbstractValidator {

    protected AbstractColumnValidator(final String validatorValue) {
        super(validatorValue);
    }

    public abstract int getErrorRowIndex(final Object object);
}
