package utils;

import java.util.Objects;

public class Utils {

    public static <T> boolean anyNotNull(T... objects) {
        for (T object : objects) {
            if(!Objects.isNull(object)){
                return true;
            }
        }
        return false;
    }

}
