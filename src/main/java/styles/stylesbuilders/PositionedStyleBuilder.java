package styles.stylesbuilders;

import positioning.Position;
import styles.PositionedStyle;
import styles.ReportStyle;

public class PositionedStyleBuilder extends AbstractReportStyleBuilder<PositionedStyle, Position> {

    PositionedStyleBuilder(final Position tuple, final boolean clonePreviousStyle) {
        super(tuple, clonePreviousStyle);
    }

    @Override
    protected PositionedStyle newStyleInstance() {
        return new PositionedStyle(this.tuple);
    }

    @Override
    protected PositionedStyle setCustomStyles(final ReportStyle style) {
        return (PositionedStyle) style;
    }
}
