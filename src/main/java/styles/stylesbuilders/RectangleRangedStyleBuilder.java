package styles.stylesbuilders;

import positioning.RectangleRange;
import styles.RectangleRangedStyle;
import styles.ReportStyle;

public class RectangleRangedStyleBuilder extends AbstractReportStyleBuilder<RectangleRangedStyle, RectangleRange> {

    RectangleRangedStyleBuilder(final RectangleRange tuple, final boolean clonePreviousStyle) {
        super(tuple, clonePreviousStyle);
    }


    @Override
    protected RectangleRangedStyle newStyleInstance() {
        return new RectangleRangedStyle(this.tuple);
    }

    @Override
    protected RectangleRangedStyle setCustomStyles(final ReportStyle style) {
        return (RectangleRangedStyle) style;
    }
}
