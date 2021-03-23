package org.greports.styles.interfaces;

import java.awt.*;
import java.util.Map;

/**
 * This interface can be used to alternate row's color.
 */
public interface StripedRows {

    /**
     * Enumerated indicating the parity of the rows to which the color is applied.
     */
    enum StripedRowsIndex {
        EVEN(0), ODD(1);

        private final int index;

        StripedRowsIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    /**
     * Map's entry key represents a report name to which the rule applies.
     * The value indicates the parity of the rows.
     * @return {@link Map}
     */
    Map<String, StripedRowsIndex> getStripedRowsIndex();

    /**
     * Map's entry key represents a report name to which the rule applies.
     * The value indicates the color to apply.
     * @return {@link Map}
     */
    Map<String, Color> getStripedRowsColor();

}
