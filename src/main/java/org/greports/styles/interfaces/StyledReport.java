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
     *
     * @return Map<String, VerticalRangedStylesBuilder> getRangedRowStyles();
     */
    Map<String, VerticalRangedStylesBuilder> getRangedRowStyles(int rowsCount);
    /**
     * Method returns a map containing report names as keys and {@link HorizontalRangedStylesBuilder} as values
     * used to apply generated styles to specified columns range
     *
     * @return Map<String, HorizontalRangedStylesBuilder> getRangedColumnStyles();
     */
    Map<String, HorizontalRangedStylesBuilder> getRangedColumnStyles();
    /**
     * Method returns a map containing report names as keys and {@link PositionedStylesBuilder} as values
     * used to apply generated styles to specified positioned cells
     *
     * @return Map<String, PositionedStylesBuilder> getPositionedStyles();
     */
    Map<String, PositionedStylesBuilder> getPositionedStyles();
    /**
     * Method returns a map containing report names as keys and {@link RectangleRangedStylesBuilder} as values
     * used to apply generated styles to specified rectangle range areas
     *
     * @return Map<String, RectangleRangedStylesBuilder> getRectangleRangedStyles();
     */
    Map<String, RectangleRangedStylesBuilder> getRectangleRangedStyles(int rowsCount);
}
