package styles.interfaces;

import styles.stylesbuilders.HorizontalRangedStylesBuilder;
import styles.stylesbuilders.PositionedStylesBuilder;
import styles.stylesbuilders.RectangleRangedStylesBuilder;
import styles.stylesbuilders.VerticalRangedStylesBuilder;

import java.util.Map;

public interface StyledReport {
    /**
     * Method returns a map containing report names as keys and ReportStylesBuilder as values
     * used to apply generated styles to specified rows range
     */
    Map<String, VerticalRangedStylesBuilder> getRangedRowStyles();
    /**
     * Method returns a map containing report names as keys and ReportStylesBuilder as values
     * used to apply generated styles to specified columns range
     */
    Map<String, HorizontalRangedStylesBuilder> getRangedColumnStyles();
    /**
     * Method returns a map containing report names as keys and ReportStylesBuilder as values
     * used to apply generated styles to specified positioned cells
     */
    Map<String, PositionedStylesBuilder> getPositionedStyles();
    /**
     * Method returns a map containing report names as keys and ReportStylesBuilder as values
     * used to apply generated styles to specified rectangle range areas
     */
    Map<String, RectangleRangedStylesBuilder> getRectangleRangedStyles();
}
