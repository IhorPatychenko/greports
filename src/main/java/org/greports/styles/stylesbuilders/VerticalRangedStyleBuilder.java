package org.greports.styles.stylesbuilders;

import org.greports.positioning.VerticalRange;
import org.greports.styles.ReportStyle;
import org.greports.styles.VerticalRangedStyle;

public class VerticalRangedStyleBuilder extends AbstractReportStyleBuilder<VerticalRangedStyle, VerticalRange, VerticalRangedStyleBuilder> {
    private static final long serialVersionUID = 5407622152704200794L;

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
