package org.greports.styles.stylesbuilders;

import org.greports.positioning.RectangleRange;
import org.greports.styles.RectangleRangedStyle;

public class RectangleRangedStylesBuilder extends AbstractReportStylesBuilder<RectangleRangedStylesBuilder, RectangleRange, RectangleRangedStyleBuilder, RectangleRangedStyle> {

    public RectangleRangedStylesBuilder(final StylePriority priority) {
        super(priority);
    }

    @Override
    protected RectangleRangedStylesBuilder getThis() {
        return this;
    }

    @Override
    protected RectangleRangedStyleBuilder getStyleBuilder(final RectangleRange tuple, final boolean clonePreviousStyle) {
        return new RectangleRangedStyleBuilder(tuple, clonePreviousStyle);
    }

    @Override
    public void mergeStyles(final RectangleRangedStylesBuilder other) {
        if (other != null) {
            styleBuilders.addAll(other.getStylesBuilders());
        }
    }
}
