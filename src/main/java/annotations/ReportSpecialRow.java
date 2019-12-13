package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ReportSpecialRow {

    /**
     *
     * @return row index value
     */
    int rowIndex();

    /**
     *
     * @return ReportSpecialCell[] with columns containing special data in the report
     */
    ReportSpecialCell[] columns() default {};
}
