package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Configuration {
    String reportName();
    String templatePath() default "";
    String sheetName() default "";
    boolean showHeader() default true;
    boolean sortableHeader() default false;
    short headerOffset() default 0;
    short dataOffset() default 1;
    SpecialRow[] specialRows() default {};
    SpecialColumn[] specialColumns() default {};
}
