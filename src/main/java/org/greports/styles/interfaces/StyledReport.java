package org.greports.styles.interfaces;

import org.greports.styles.stylesbuilders.HorizontalRangedStylesBuilder;
import org.greports.styles.stylesbuilders.PositionedStylesBuilder;
import org.greports.styles.stylesbuilders.RectangleRangedStylesBuilder;
import org.greports.styles.stylesbuilders.VerticalRangedStylesBuilder;

import java.util.Map;

public interface StyledReport {
    /**
     * Method returns a map containing report names as keys and {@link VerticalRangedStylesBuilder} as values
     * used to apply generated styles to specified rows range
     */
    Map<String, VerticalRangedStylesBuilder> getRangedRowStyles();
    /**
     * Method returns a map containing report names as keys and {@link HorizontalRangedStylesBuilder} as values
     * used to apply generated styles to specified columns range
     */
    Map<String, HorizontalRangedStylesBuilder> getRangedColumnStyles();
    /**
     * Method returns a map containing report names as keys and {@link PositionedStylesBuilder} as values
     * used to apply generated styles to specified positioned cells
     */
    Map<String, PositionedStylesBuilder> getPositionedStyles();
    /**
     * Method returns a map containing report names as keys and {@link RectangleRangedStylesBuilder} as values
     * used to apply generated styles to specified rectangle range areas
     */
    Map<String, RectangleRangedStylesBuilder> getRectangleRangedStyles();
}
