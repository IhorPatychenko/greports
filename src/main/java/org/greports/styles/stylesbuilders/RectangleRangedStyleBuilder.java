package org.greports.styles.stylesbuilders;

import org.greports.positioning.HorizontalRange;
import org.greports.positioning.RectangleRange;
import org.greports.positioning.VerticalRange;
import org.greports.styles.RectangleRangedStyle;
import org.greports.styles.ReportStyle;

public class RectangleRangedStyleBuilder extends AbstractReportStyleBuilder<RectangleRangedStyle, RectangleRange, RectangleRangedStyleBuilder> {
    private static final long serialVersionUID = 6761881839573020152L;

    private RectangleRangedStyleBuilder(final AbstractReportStyleBuilder<?,?,?> abstractReportStyleBuilder, final RectangleRange rectangleRange) {
        super(rectangleRange, abstractReportStyleBuilder.clonePreviousStyle);
        this.foregroundColor = abstractReportStyleBuilder.foregroundColor;
        this.fontColor = abstractReportStyleBuilder.fontColor;
        this.fillPattern = abstractReportStyleBuilder.fillPattern;
        this.boldFont = abstractReportStyleBuilder.boldFont;
        this.italicFont = abstractReportStyleBuilder.italicFont;
        this.underlineFont = abstractReportStyleBuilder.underlineFont;
        this.strikeoutFont = abstractReportStyleBuilder.strikeoutFont;
        this.horizontalAlignment = abstractReportStyleBuilder.horizontalAlignment;
        this.verticalAlignment = abstractReportStyleBuilder.verticalAlignment;
        this.borderTop = abstractReportStyleBuilder.borderTop;
        this.borderRight = abstractReportStyleBuilder.borderRight;
        this.borderBottom = abstractReportStyleBuilder.borderBottom;
        this.borderLeft = abstractReportStyleBuilder.borderLeft;
        this.borderColor = abstractReportStyleBuilder.borderColor;
    }

    public RectangleRangedStyleBuilder(final HorizontalRangedStyleBuilder horizontalRangedStyleBuilder, final Integer rowIndex) {
        this(horizontalRangedStyleBuilder, new RectangleRange(
                new VerticalRange(rowIndex, rowIndex),
                horizontalRangedStyleBuilder.tuple
        ));
    }

    public RectangleRangedStyleBuilder(final PositionedStyleBuilder positionedStyleBuilder) {
        this(positionedStyleBuilder, new RectangleRange(
            new VerticalRange(positionedStyleBuilder.tuple.getRow(), positionedStyleBuilder.tuple.getRow()),
            new HorizontalRange(positionedStyleBuilder.tuple.getColumn(), positionedStyleBuilder.tuple.getColumn())
        ));
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
