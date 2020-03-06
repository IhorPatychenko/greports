package org.greports.styles;

import org.greports.positioning.VerticalRange;

public class VerticalRangedStyle extends ReportStyle<VerticalRange> {
    private static final long serialVersionUID = -7534724944790840026L;

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
