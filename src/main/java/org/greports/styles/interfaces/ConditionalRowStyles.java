package org.greports.styles.interfaces;

import org.greports.positioning.HorizontalRange;
import org.greports.styles.stylesbuilders.ReportStyleBuilder;

import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;

/**
 * With this interface is possible to define the logic to apply styles
 * based on a row's index.
 */
public interface ConditionalRowStyles {

    /**
     * Map's entry key represents a report name to which the rule applies.
     * The value of the map is an {@link IntPredicate} that returns {@code true}
     * if the row is styled. The parameter of {@link IntPredicate}
     * is an zero-based row index.
     * @return {@link Map}
     */
    Map<String, IntPredicate> isStyled();

    /**
     * Map's entry key represents a report name to which the styles applies.
     * Map's value a is {@link List} which contains style builders tobe applied
     * if the condition is met.
     * @return {@link Map}
     */
    Map<String, List<ReportStyleBuilder<HorizontalRange>>> getIndexBasedStyle();
}
