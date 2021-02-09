package org.greports.validators;

import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ValidatorFactory {

    private ValidatorFactory() {
    }

    protected static final Map<Pair<Class<? extends AbstractValidator>, String>, AbstractValidator> _validators = new HashMap<>();

    public static AbstractValidator get(Class<? extends AbstractValidator> clazz, String value) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Pair<Class<? extends AbstractValidator>, String> pair = Pair.of(clazz, value);
        if(!_validators.containsKey(pair)){
            Constructor<? extends AbstractValidator> constructor = clazz.getDeclaredConstructor(String.class);
            constructor.setAccessible(true);
            AbstractValidator validator = constructor.newInstance(value);
            _validators.put(pair, validator);
        }
        return _validators.get(pair);
    }

}
