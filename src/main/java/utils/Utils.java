package utils;

import java.util.Objects;

public class Utils {

    @SafeVarargs
    public static <T> boolean anyNotNull(T... objects) {
        for (T object : objects) {
            if(!Objects.isNull(object)){
                return true;
            }
        }
        return false;
    }

    public static String capitalizeString(String str) {
        if(str != null){
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return null;
    }
}
