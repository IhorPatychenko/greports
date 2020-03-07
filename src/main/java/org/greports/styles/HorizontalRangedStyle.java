package org.greports.styles;

import org.greports.positioning.HorizontalRange;

public class HorizontalRangedStyle extends ReportStyle<HorizontalRange> {
    private static final long serialVersionUID = -3383227696134452075L;

    private Integer columnWidth;
    private Boolean wrapText;

    public HorizontalRangedStyle(final HorizontalRange tuple) {
        super(tuple);
    }

    public HorizontalRangedStyle setColumnWidth(final Integer columnWidth) {
        this.columnWidth = columnWidth;
        return this;
    }

    public HorizontalRangedStyle setWrapText(final Boolean wrapText) {
        this.wrapText = wrapText;
        return this;
    }

    public Integer getColumnWidth() {
        return columnWidth;
    }

    public Boolean getWrapText() {
        return wrapText;
    }
}
