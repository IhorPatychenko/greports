package org.greports.styles.interfaces;

import org.greports.styles.stylesbuilders.VerticalRangedStyleBuilder;

import java.util.Map;

public interface ConditionalRowStyles {
    boolean isStyled(final int rowIndex);
    Map<String, VerticalRangedStyleBuilder> getIndexBasedStyle();
}
