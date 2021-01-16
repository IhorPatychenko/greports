package org.greports.styles.interfaces;

import org.greports.positioning.HorizontalRange;
import org.greports.styles.stylesbuilders.ReportStyleBuilder;

import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;

public interface ConditionalRowStyles {
    Map<String, IntPredicate> isStyled();
    Map<String, List<ReportStyleBuilder<HorizontalRange>>> getIndexBasedStyle();
}
