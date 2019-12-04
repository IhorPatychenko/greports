package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Report {
    String[] name();
    boolean showHeader() default true;
    String translationsDir() default "src/main/java/resources/i18n/";
    ReportTemplate[] templates() default {};
    ReportColumn[] emptyColumns() default {};
}
