package annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Repeatable(value = ReportColumns.class)
public @interface ReportColumn {
    String reportName();
    String position();
    String title();
    String format() default "";
    String id() default "";
    boolean columnForLoader() default false;
}