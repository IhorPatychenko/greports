package org.greports.styles.stylesbuilders;

import org.greports.positioning.RectangleRange;
import org.greports.styles.RectangleRangedStyle;
import org.greports.styles.ReportStyle;

public class RectangleRangedStyleBuilder extends AbstractReportStyleBuilder<RectangleRangedStyle, RectangleRange, RectangleRangedStyleBuilder> {

    RectangleRangedStyleBuilder(final RectangleRange rectangleRange, final boolean clonePreviousStyle) {
        super(rectangleRange, clonePreviousStyle);
    }

    @Override
    protected RectangleRangedStyleBuilder getThis() {
        return this;
    }


    @Override
    protected RectangleRangedStyle newStyleInstance() {
        return new RectangleRangedStyle(this.tuple);
    }

    @Override
    protected RectangleRangedStyle setCustomStyles(final ReportStyle<RectangleRange> style) {
        return (RectangleRangedStyle) style;
    }
}
