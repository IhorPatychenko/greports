package styles.stylesbuilders;

import positioning.HorizontalRange;
import styles.HorizontalRangedStyle;
import styles.ReportStyle;

public class HorizontalRangedStyleBuilder extends AbstractReportStyleBuilder<HorizontalRangedStyle, HorizontalRange> {

    private Integer columnWidth;

    HorizontalRangedStyleBuilder(final HorizontalRange tuple, final boolean clonePreviousStyle) {
        super(tuple, clonePreviousStyle);
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
