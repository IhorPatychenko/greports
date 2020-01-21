package styles;

import positioning.VerticalRange;

public class VerticalRangedStyle extends ReportStyle<VerticalRange> {

    private Float rowHeight;

    public VerticalRangedStyle(final VerticalRange tuple) {
        super(tuple);
    }

    public void setRowHeight(final Float rowHeight) {
        this.rowHeight = rowHeight;
    }

    public Float getRowHeight() {
        return rowHeight;
    }
}
