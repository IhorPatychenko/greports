package styles.stylesbuilders;

import positioning.HorizontalRange;
import styles.HorizontalRangedStyle;
import styles.ReportStyle;

public class HorizontalRangedStyleBuilder extends AbstractReportStyleBuilder<HorizontalRangedStyle, HorizontalRange, HorizontalRangedStyleBuilder> {

    private Integer columnWidth;

    HorizontalRangedStyleBuilder(final HorizontalRange horizontalRange, final boolean clonePreviousStyle) {
        super(horizontalRange, clonePreviousStyle);
    }

    @Override
    protected HorizontalRangedStyleBuilder getThis() {
        return this;
    }

    public void setColumnWidth(final Integer columnWidth) {
        this.columnWidth = columnWidth;
    }

    @Override
    protected HorizontalRangedStyle newStyleInstance() {
        return new HorizontalRangedStyle(this.tuple);
    }

    @Override
    protected HorizontalRangedStyle setCustomStyles(final ReportStyle<HorizontalRange> style) {
        final HorizontalRangedStyle rangedStyle = (HorizontalRangedStyle) style;
        rangedStyle.setColumnWidth(this.columnWidth);
        return rangedStyle;
    }
}
