package org.greports.interfaces.group;

import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/**
 * An interface which defines grouped rows for every report indicated.
 */
public interface GroupedRows {
    /**
     * The key represents a report name.
     * The value represents a predicate which returns {@code true} or {@code false}
     * if the current row is first row in the group.
     * @return {@link Map}
     */
    Map<String, Predicate<Integer>> isGroupStartRow();

    /**
     * The key represents a report name.
     * The value represents a predicate which returns {@code true} or {@code false}
     * if the current row is last row in the group.
     * @return {@link Map}
     */
    Map<String, Predicate<Integer>> isGroupEndRow();

    /**
     * The key represents a report name.
     * The {@link BooleanSupplier} returns {@code true} or {@code false}
     * which indicates if all groups are collapsed by default.
     * @return {@link Map}
     */
    Map<String, BooleanSupplier> isRowCollapsedByDefault();
}
