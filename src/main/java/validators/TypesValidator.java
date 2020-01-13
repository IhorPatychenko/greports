package validators;

public class TypesValidator extends AbstractValidator {

    private Class<?> clazz;

    public TypesValidator(final Class<?> clazz) {
        super(clazz.getName());
        this.clazz = clazz;
    }

    @Override
    public boolean isValid(final Object object) {
        final Class<?> aClass = (Class<?>) object;
        return aClass.equals(this.clazz);
    }

    @Override
    public String getDefaultErrorMessage() {
        return "Incompatible types";
    }

    @Override
    public String getErrorKey() {
        return "Validator.IncompatibleTypes." + clazz.getSimpleName();
    }
}
