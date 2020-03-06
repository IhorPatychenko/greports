package org.greports.styles;

import org.greports.positioning.HorizontalRange;

public class HorizontalRangedStyle extends ReportStyle<HorizontalRange> {
    private static final long serialVersionUID = -3383227696134452075L;

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
