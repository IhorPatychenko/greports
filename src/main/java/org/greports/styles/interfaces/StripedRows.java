package org.greports.styles.interfaces;

import java.awt.*;
import java.util.Map;

public interface StripedRows {

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

    Map<String, StripedRowsIndex> getStripedRowsIndex();
    Map<String, Color> getStripedRowsColor();

}
