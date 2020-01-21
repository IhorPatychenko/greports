package styles.stylesbuilders;

import positioning.VerticalRange;
import styles.ReportStyle;
import styles.VerticalRangedStyle;

public class VerticalRangedStyleBuilder extends AbstractReportStyleBuilder<VerticalRangedStyle, VerticalRange> {

    private Float rowHeight;

    VerticalRangedStyleBuilder(final VerticalRange tuple, final boolean clonePreviousStyle) {
        super(tuple, clonePreviousStyle);
    }

    protected void setRowHeight(Float rowHeight) {
        this.rowHeight = rowHeight;
    }

    @Override
    protected VerticalRangedStyle newStyleInstance() {
        return new VerticalRangedStyle(this.tuple);
    }

    @Override
    protected VerticalRangedStyle setCustomStyles(final ReportStyle<VerticalRange> style) {
        final VerticalRangedStyle rangedStyle = (VerticalRangedStyle) style;
        rangedStyle.setRowHeight(rowHeight);
        return rangedStyle;
    }
}
