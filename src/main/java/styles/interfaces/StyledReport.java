package styles.interfaces;

import styles.HorizontalRangedStyle;
import styles.PositionedStyle;
import styles.RectangleRangedStyle;
import styles.ReportStylesBuilder;
import styles.VerticalRangedStyle;

import java.util.Map;

public interface StyledReport {
    /**
     * Method returns a map containing report names as keys and ReportStylesBuilder as values
     * used to apply generated styles to specified rows range
     */
    Map<String, ReportStylesBuilder<VerticalRangedStyle>> getRangedRowStyles();
    /**
     * Method returns a map containing report names as keys and ReportStylesBuilder as values
     * used to apply generated styles to specified columns range
     */
    Map<String, ReportStylesBuilder<HorizontalRangedStyle>> getRangedColumnStyles();
    /**
     * Method returns a map containing report names as keys and ReportStylesBuilder as values
     * used to apply generated styles to specified positioned cells
     */
    Map<String, ReportStylesBuilder<PositionedStyle>> getPositionedStyles();
    /**
     * Method returns a map containing report names as keys and ReportStylesBuilder as values
     * used to apply generated styles to specified rectangle range areas
     */
    Map<String, ReportStylesBuilder<RectangleRangedStyle>> getRectangleRangedStyles();
}
