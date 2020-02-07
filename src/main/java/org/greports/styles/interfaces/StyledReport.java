package org.greports.styles.interfaces;

import org.greports.styles.stylesbuilders.HorizontalRangedStylesBuilder;
import org.greports.styles.stylesbuilders.PositionedStylesBuilder;
import org.greports.styles.stylesbuilders.RectangleRangedStylesBuilder;
import org.greports.styles.stylesbuilders.VerticalRangedStylesBuilder;

import java.util.Map;

public interface StyledReport {
    /**
     * Method returns a map containing report names as keys and ReportStylesBuilder as values
     * used to apply generated org.greports.styles to specified rows range
     */
    Map<String, VerticalRangedStylesBuilder> getRangedRowStyles();
    /**
     * Method returns a map containing report names as keys and ReportStylesBuilder as values
     * used to apply generated org.greports.styles to specified columns range
     */
    Map<String, HorizontalRangedStylesBuilder> getRangedColumnStyles();
    /**
     * Method returns a map containing report names as keys and ReportStylesBuilder as values
     * used to apply generated org.greports.styles to specified positioned cells
     */
    Map<String, PositionedStylesBuilder> getPositionedStyles();
    /**
     * Method returns a map containing report names as keys and ReportStylesBuilder as values
     * used to apply generated org.greports.styles to specified rectangle range areas
     */
    Map<String, RectangleRangedStylesBuilder> getRectangleRangedStyles();
}
