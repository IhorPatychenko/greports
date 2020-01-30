package styles.interfaces;

import styles.stylesbuilders.VerticalRangedStyleBuilder;

import java.util.Map;

public interface IndexBasedRowStyle {
    boolean isStyled(int rowIndex);
    Map<String, VerticalRangedStyleBuilder> getIndexBasedStyle();
}
