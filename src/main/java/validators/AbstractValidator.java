package validators;

import utils.Pair;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractValidator {

    protected static Map<Pair<Class<? extends AbstractValidator>, String>, AbstractValidator> _validators = new HashMap<>();

    protected final String value;
    protected final String errorMessage;

    protected AbstractValidator(final String value, final String errorMessage) {
        this.value = value;
        this.errorMessage = errorMessage;
    }

    public static AbstractValidator getValidatorOrNull(Class<? extends AbstractValidator> validatorClass, String value){
        return _validators.getOrDefault(Pair.of(validatorClass, value), null);
    }

    public String getValue() {
        return value;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public abstract boolean isValid(Object object);
}
