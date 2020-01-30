package styles.stylesbuilders;

import positioning.Position;
import styles.PositionedStyle;
import styles.ReportStyle;

public class PositionedStyleBuilder extends AbstractReportStyleBuilder<PositionedStyle, Position, PositionedStyleBuilder> {

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
