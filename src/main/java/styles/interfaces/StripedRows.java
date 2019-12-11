package styles.interfaces;

import org.apache.poi.ss.usermodel.IndexedColors;

import java.util.Map;

public interface StripedRows {

    enum StripedRowsIndex {
        EVEN(0), ODD(1);

        private int index;
        StripedRowsIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    Map<String, StripedRowsIndex> getStripedRowsIndex();
    Map<String, IndexedColors> getStripedRowsColor();

}
