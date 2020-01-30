package styles.stylesbuilders;

import positioning.VerticalRange;
import styles.ReportStyle;
import styles.VerticalRangedStyle;

public class VerticalRangedStyleBuilder extends AbstractReportStyleBuilder<VerticalRangedStyle, VerticalRange, VerticalRangedStyleBuilder> {

    private Float rowHeight;

    public VerticalRangedStyleBuilder(final boolean clonePreviousStyle) {
        this(null, clonePreviousStyle);
    }

    protected VerticalRangedStyleBuilder(final VerticalRange verticalRange, final boolean clonePreviousStyle) {
        super(verticalRange, clonePreviousStyle);
    }

    @Override
    protected VerticalRangedStyleBuilder getThis() {
        return this;
    }

    public VerticalRangedStyleBuilder setRowHeight(Float rowHeight) {
        this.rowHeight = rowHeight;
        return this;
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
