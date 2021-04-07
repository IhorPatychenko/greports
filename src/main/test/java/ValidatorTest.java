package java;

import org.greports.validators.BooleanValidator;
import org.greports.validators.DateValidator;
import org.greports.validators.DoubleValidator;
import org.greports.validators.FloatValidator;
import org.greports.validators.IntegerValidator;
import org.greports.validators.LongValidator;
import org.greports.validators.NotNullValidator;
import org.greports.validators.NumberValidator;
import org.greports.validators.ShortValidator;
import org.greports.validators.StringValidator;
import org.greports.validators.UniqueValueValidator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidatorTest {

    @Test
    void booleanValidatorTest() {
        final BooleanValidator booleanValidator = new BooleanValidator(null);
        assertTrue(booleanValidator.isValid(Boolean.TRUE));
        assertTrue(booleanValidator.isValid(Boolean.FALSE));
        assertTrue(booleanValidator.isValid(true));
        assertTrue(booleanValidator.isValid(false));
    }

    @Test
    void dateValidatorTest() {
        final DateValidator dateValidator = new DateValidator(null);
        assertTrue(dateValidator.isValid(new Date()));
        assertFalse(dateValidator.isValid(null));
    }

    @Test
    void doubleValidatorTest() {
        final DoubleValidator doubleValidator = new DoubleValidator(null);
        assertTrue(doubleValidator.isValid(1D));
        assertTrue(doubleValidator.isValid(new Double("1")));
        assertFalse(doubleValidator.isValid(1));
    }

    @Test
    void floatValidatorTest() {
        final FloatValidator floatValidator = new FloatValidator(null);
        assertTrue(floatValidator.isValid(1F));
        assertTrue(floatValidator.isValid(new Float("1")));
        assertFalse(floatValidator.isValid(1));
    }

    @Test
    void integerValidatorTest() {
        final IntegerValidator integerValidator = new IntegerValidator(null);
        assertTrue(integerValidator.isValid(1));
        assertTrue(integerValidator.isValid(new Integer("1")));
        assertFalse(integerValidator.isValid("1"));
    }

    @Test
    void longValidatorTest() {
        final LongValidator longValidator = new LongValidator(null);
        assertTrue(longValidator.isValid(1L));
        assertTrue(longValidator.isValid(new Long("1")));
        assertFalse(longValidator.isValid("1"));
    }

    @Test
    void notNullValidatorTest() {
        final NotNullValidator notNullValidator = new NotNullValidator(null);
        assertTrue(notNullValidator.isValid("true"));
        assertFalse(notNullValidator.isValid(null));
    }

    @Test
    void shortValidatorTest() {
        final ShortValidator shortValidator = new ShortValidator(null);
        assertTrue(shortValidator.isValid((short) 1));
        assertTrue(shortValidator.isValid(new Short("1")));
        assertFalse(shortValidator.isValid(1));
    }

    @Test
    void numberValidatorTest() {
        final NumberValidator numberValidator = new NumberValidator(null);
        assertTrue(numberValidator.isValid(1));
        assertTrue(numberValidator.isValid(1L));
        assertTrue(numberValidator.isValid(1D));
        assertTrue(numberValidator.isValid(1F));
        assertFalse(numberValidator.isValid("1"));
        assertFalse(numberValidator.isValid(new Date()));
    }

    @Test
    void stringValidatorTest() {
        final StringValidator stringValidator = new StringValidator(null);
        assertTrue(stringValidator.isValid("Test"));
        assertFalse(stringValidator.isValid(1));
        assertFalse(stringValidator.isValid(new Date()));
    }

    @Test
    void uniqueValueValidatorTest() {
        final UniqueValueValidator uniqueValueValidator = new UniqueValueValidator(null);
        assertTrue(uniqueValueValidator.isValid(Arrays.asList(1, 2, 3, 4, 5)));
        assertFalse(uniqueValueValidator.isValid(Arrays.asList(1, 2, 3, 4, 4)));
        assertFalse(uniqueValueValidator.isValid(Arrays.asList(1, 2, 3, 4, 1)));
        assertFalse(uniqueValueValidator.isValid(Arrays.asList(1, 1, 3, 4, 5)));
        assertFalse(uniqueValueValidator.isValid(Arrays.asList(1, 2, 2, 4, 5)));

        assertTrue(uniqueValueValidator.isValid(Arrays.asList("String 1", "String 2")));
        assertFalse(uniqueValueValidator.isValid(Arrays.asList("String 2", "String 2")));
        assertFalse(uniqueValueValidator.isValid(Arrays.asList("String 2", "String 1", "String 2")));
    }
}
