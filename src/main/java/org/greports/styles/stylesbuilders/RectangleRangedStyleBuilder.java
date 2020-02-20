package org.greports.styles.stylesbuilders;

import org.greports.positioning.RectangleRange;
import org.greports.positioning.VerticalRange;
import org.greports.styles.RectangleRangedStyle;
import org.greports.styles.ReportStyle;

public class RectangleRangedStyleBuilder extends AbstractReportStyleBuilder<RectangleRangedStyle, RectangleRange, RectangleRangedStyleBuilder> {

    public RectangleRangedStyleBuilder(final HorizontalRangedStyleBuilder horizontalRangedStyleBuilder, final Integer rowIndex) {
        super(new RectangleRange(new VerticalRange(rowIndex, rowIndex), horizontalRangedStyleBuilder.tuple), horizontalRangedStyleBuilder.clonePreviousStyle);
        this.foregroundColor = horizontalRangedStyleBuilder.foregroundColor;
        this.fontColor = horizontalRangedStyleBuilder.fontColor;
        this.fillPattern = horizontalRangedStyleBuilder.fillPattern;
        this.boldFont = horizontalRangedStyleBuilder.boldFont;
        this.italicFont = horizontalRangedStyleBuilder.italicFont;
        this.underlineFont = horizontalRangedStyleBuilder.underlineFont;
        this.strikeoutFont = horizontalRangedStyleBuilder.strikeoutFont;
        this.horizontalAlignment = horizontalRangedStyleBuilder.horizontalAlignment;
        this.verticalAlignment = horizontalRangedStyleBuilder.verticalAlignment;
        this.borderTop = horizontalRangedStyleBuilder.borderTop;
        this.borderRight = horizontalRangedStyleBuilder.borderRight;
        this.borderBottom = horizontalRangedStyleBuilder.borderBottom;
        this.borderLeft = horizontalRangedStyleBuilder.borderLeft;
        this.borderColor = horizontalRangedStyleBuilder.borderColor;
    }

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
