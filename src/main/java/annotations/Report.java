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
    String sheetName() default "";
    boolean showHeader() default true;
    short headerOffset() default 0;
    short dataOffset() default 1;
    boolean sortableHeader() default false;
    ReportTemplate[] templates() default {};
    ReportColumn[] emptyColumns() default {};
    ReportSpecialRow[] specialRows() default {};
}
