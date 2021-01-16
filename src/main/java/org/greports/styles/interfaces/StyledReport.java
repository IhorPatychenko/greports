package org.greports.styles.interfaces;

import org.greports.styles.stylesbuilders.ReportStylesBuilder;

import java.util.Map;

public interface StyledReport {
    /**
     * Method returns a map containing report names as keys and {@link ReportStylesBuilder} as values
     * used to apply generated styles to specified rectangle range areas
     *
     * @return {@link Map}
     */
    Map<String, ReportStylesBuilder> getReportStyles(int rowsCount);
}
