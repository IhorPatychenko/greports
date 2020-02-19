package org.greports.styles.interfaces;

import org.greports.styles.stylesbuilders.VerticalRangedStyleBuilder;

import java.util.Map;
import java.util.function.Predicate;

public interface ConditionalRowStyles {
    Map<String, Predicate<Integer>> isStyled();
    Map<String, VerticalRangedStyleBuilder> getIndexBasedStyle();
}
