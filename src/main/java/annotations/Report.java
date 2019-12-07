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
    boolean showHeader() default true;
    short headerStartRow() default 0;
    short dataStartRow() default 1;
    ReportTemplate[] templates() default {};
    ReportColumn[] emptyColumns() default {};
}
