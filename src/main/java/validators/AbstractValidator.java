package validators;

import exceptions.ReportEngineValidationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractValidator {

    private static Map<Class<? extends AbstractValidator>, AbstractValidator> _validators = new HashMap<>();

    public AbstractValidator() {}

    public static AbstractValidator getValidator(Class<? extends AbstractValidator> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if(!_validators.containsKey(clazz)){
            Constructor<? extends AbstractValidator> constructor = clazz.getDeclaredConstructor();
            AbstractValidator validator = constructor.newInstance();
            _validators.put(clazz, validator);
        }
        return _validators.get(clazz);
    }

    public abstract void validate(Object value) throws ReportEngineValidationException;
    public abstract String getErrorMessage();
    public abstract String getErrorKey();
}
