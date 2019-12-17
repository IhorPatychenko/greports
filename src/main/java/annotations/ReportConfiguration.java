package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ReportConfiguration {
    String name();
    String sheetName();
    boolean showHeader() default true;
    boolean sortableHeader() default false;
    short headerOffset() default 0;
    short dataOffset() default 1;
    ReportColumn[] emptyColumns() default {};
    ReportSpecialRow[] specialRows() default {};
}
