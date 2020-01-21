package styles.stylebuilders;

import positioning.VerticalRange;
import styles.VerticalRangedStyle;

public class VerticalRangedStyleBuilder extends ReportStyleBuilder<VerticalRangedStyle, VerticalRange> {
    VerticalRangedStyleBuilder(VerticalRangedStyle tuple, boolean clonePreviousStyle) {
        super(tuple, clonePreviousStyle);
    }

    @Override
    protected VerticalRange buildStyle() {
        return null;
    }
}
