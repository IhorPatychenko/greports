package org.greports.styles.interfaces;

import org.greports.styles.stylesbuilders.ReportStylesBuilder;

import java.util.Map;

/**
 * An interface to implement create the definition of report styles.
 */
public interface StyledReport {
    /**
     * Method returns a map containing report names as keys and {@link ReportStylesBuilder} as values
     * used to apply generated styles to specified rectangle range areas
     *
     * @return {@link Map}
     */
    Map<String, ReportStylesBuilder> getReportStyles(Integer rowsCount);
}
