package org.greports.interfaces.group;

import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public interface GroupedRows {
    Map<String, Predicate<Integer>> isGroupStartRow();
    Map<String, Predicate<Integer>> isGroupEndRow();
    Map<String, BooleanSupplier> isRowCollapsedByDefault();
}
