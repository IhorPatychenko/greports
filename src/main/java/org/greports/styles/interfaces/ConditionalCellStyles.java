package org.greports.styles.interfaces;

import org.apache.commons.lang3.tuple.Pair;
import org.greports.positioning.Position;
import org.greports.styles.stylesbuilders.ReportStyleBuilder;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface ConditionalCellStyles {
    Map<String, List<Pair<String, Predicate<Integer>>>> isCellStyled();
    Map<String, List<Pair<String, ReportStyleBuilder<Position>>>> getIndexBasedCellStyle();
}
