package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to allow multiple subreports definition for different reports.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Subreports {
    /**
     * @return an array of {@link Subreport}
     */
    Subreport[] value();
}
