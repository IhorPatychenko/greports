package styles.interfaces;

import styles.stylebuilders.HorizontalRangedStyleBuilder;
import styles.stylebuilders.PositionedStyleBuilder;
import styles.stylebuilders.RectangleRangedStyleBuilder;
import styles.stylebuilders.VerticalRangedStylesBuilder;

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
    Map<String, HorizontalRangedStyleBuilder> getRangedColumnStyles();
    /**
     * Method returns a map containing report names as keys and ReportStylesBuilder as values
     * used to apply generated styles to specified positioned cells
     */
    Map<String, PositionedStyleBuilder> getPositionedStyles();
    /**
     * Method returns a map containing report names as keys and ReportStylesBuilder as values
     * used to apply generated styles to specified rectangle range areas
     */
    Map<String, RectangleRangedStyleBuilder> getRectangleRangedStyles();
}
