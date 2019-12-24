package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be placed on the Data Transfer Object class to be passed to @ReportGenerator
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Report {
    String translationsDir() default "src/main/java/resources/i18n/";
    Configuration[] reportConfigurations() default {};
}
