package org.greports.interfaces;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public interface GroupedColumns {
    Map<String, List<Pair<Integer, Integer>>> getColumnGroupRanges();
    Map<String, BooleanSupplier> isColumnsCollapsedByDefault();
}
