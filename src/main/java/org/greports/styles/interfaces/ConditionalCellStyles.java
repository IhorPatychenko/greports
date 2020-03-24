package org.greports.styles.interfaces;

import org.greports.styles.stylesbuilders.PositionedStyleBuilder;
import org.greports.utils.Pair;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface ConditionalCellStyles {
    Map<String, List<Pair<String, Predicate<Integer>>>> isCellStyled();
    Map<String, List<Pair<String, PositionedStyleBuilder>>> getIndexBasedCellStyle();
}
