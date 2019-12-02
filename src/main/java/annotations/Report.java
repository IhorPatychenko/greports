package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Report {
    String[] name();
    String translationsDir() default "src/main/java/resources/i18n/";
    String lang() default "en";
    ReportTemplate[] templates() default {};
    ReportColumn[] emptyColumns() default {};
}
