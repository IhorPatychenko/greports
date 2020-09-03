package org.greports.styles.interfaces;

import org.greports.styles.stylesbuilders.HorizontalRangedStyleBuilder;

import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;

public interface ConditionalRowStyles {
    Map<String, IntPredicate> isStyled();
    Map<String, List<HorizontalRangedStyleBuilder>> getIndexBasedStyle();
}
