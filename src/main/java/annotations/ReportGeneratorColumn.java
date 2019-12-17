package annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Repeatable(value = ReportGeneratorColumns.class)
public @interface ReportGeneratorColumn {
    String reportName();
    String position();
    String title() default "";
    String format() default "";
    String id() default "";
    boolean autoSizeColumn() default false;
}