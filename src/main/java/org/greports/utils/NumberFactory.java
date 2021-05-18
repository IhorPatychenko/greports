package org.greports.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberFactory {
    
    public static Object valueOf(Double number, Class<?> clazz) {
        if (clazz.equals(Double.class) || clazz.equals(double.class)) {
            return number;
        } else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
            return number.intValue();
        } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
            return number.longValue();
        } else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
            return number.floatValue();
        } else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
            return number.shortValue();
        } else if(clazz.equals(BigDecimal.class)) {
            return BigDecimal.valueOf(number);
        } else if(clazz.equals(BigInteger.class)) {
            return BigInteger.valueOf(number.intValue());
        } else if(clazz.equals(Byte.class) || clazz.equals(byte.class)) {
            return number.byteValue();
        }
        return number;
    }
    
}
