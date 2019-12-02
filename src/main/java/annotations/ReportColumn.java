package annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = ReportColumns.class)
public @interface ReportColumn {
    String reportName();
    String position();
    String title();
    String value() default "";
}