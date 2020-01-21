package styles.stylesbuilders;

import positioning.Position;
import styles.PositionedStyle;

public class PositionedStylesBuilder extends AbstractReportStylesBuilder<PositionedStylesBuilder, Position, PositionedStyleBuilder, PositionedStyle> {

    public PositionedStylesBuilder(final StylePriority priority) {
        super(priority);
    }

    @Override
    protected PositionedStylesBuilder getThis() {
        return this;
    }

    @Override
    protected PositionedStyleBuilder getStyleBuilder(final Position tuple, final boolean clonePreviousStyle) {
        return new PositionedStyleBuilder(tuple, clonePreviousStyle);
    }

    @Override
    public void mergeStyles(final PositionedStylesBuilder other) {
        if (other != null) {
            styleBuilders.addAll(other.getStylesBuilders());
        }
    }
}
