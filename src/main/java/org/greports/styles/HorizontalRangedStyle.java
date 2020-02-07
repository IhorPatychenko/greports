package org.greports.styles;

import org.greports.positioning.HorizontalRange;

public class HorizontalRangedStyle extends ReportStyle<HorizontalRange> {

    private Integer columnWidth;

    public HorizontalRangedStyle(final HorizontalRange tuple) {
        super(tuple);
    }

    public void setColumnWidth(final Integer columnWidth) {
        this.columnWidth = columnWidth;
    }

    public Integer getColumnWidth() {
        return columnWidth;
    }
}
