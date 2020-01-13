package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Configuration {
    String reportName();
    String translationsDir() default "i18n/";
    String reportLang() default "en";
    String templatePath() default "";
    String sheetName() default "";
    boolean showHeader() default true;
    boolean sortableHeader() default false;
    short headerRowIndex() default 0;
    SpecialRow[] specialRows() default {};
    SpecialColumn[] specialColumns() default {};
}
