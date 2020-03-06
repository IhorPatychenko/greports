package org.greports.styles.stylesbuilders;

import org.greports.positioning.Position;
import org.greports.styles.PositionedStyle;
import org.greports.styles.ReportStyle;

public class PositionedStyleBuilder extends AbstractReportStyleBuilder<PositionedStyle, Position, PositionedStyleBuilder> {
    private static final long serialVersionUID = 2793213216575883324L;

    PositionedStyleBuilder(final Position position, final boolean clonePreviousStyle) {
        super(position, clonePreviousStyle);
    }

    @Override
    protected PositionedStyleBuilder getThis() {
        return this;
    }

    @Override
    protected PositionedStyle newStyleInstance() {
        return new PositionedStyle(this.tuple);
    }

    @Override
    protected PositionedStyle setCustomStyles(final ReportStyle<Position> style) {
        return (PositionedStyle) style;
    }
}
