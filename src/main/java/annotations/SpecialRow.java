package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface SpecialRow {

    /**
     *
     * @return row index value
     */
    int rowIndex();

    /**
     *
     * @return ReportSpecialCell[] with cells containing special data in the report
     */
    SpecialRowCell[] cells() default {};
}
