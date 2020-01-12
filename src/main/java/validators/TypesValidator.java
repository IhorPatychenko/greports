package validators;

public class TypesValidator extends AbstractValidator {

    public TypesValidator(final String value) {
        super(value);
    }

    @Override
    public boolean isValid(final Object object) {
        return object == null || value.equals(object.toString());
    }

    @Override
    public String getDefaultErrorMessage() {
        return "Incompatible types";
    }

    @Override
    public String getErrorKey() {
        return "Validator.IncompatibleTypes";
    }
}
