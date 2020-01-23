package validators;

public class AbstractColumnValidator extends AbstractValidator {

    protected AbstractColumnValidator(final String validatorValue) {
        super(validatorValue);
    }

    @Override
    public boolean isValid(final Object object) {
        return false;
    }
}
