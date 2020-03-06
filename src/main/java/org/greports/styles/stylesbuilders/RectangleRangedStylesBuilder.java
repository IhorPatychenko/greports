package org.greports.styles.stylesbuilders;

import org.greports.positioning.RectangleRange;
import org.greports.styles.RectangleRangedStyle;

import java.io.Serializable;

public class RectangleRangedStylesBuilder extends AbstractReportStylesBuilder<RectangleRangedStylesBuilder, RectangleRange, RectangleRangedStyleBuilder, RectangleRangedStyle> implements Serializable {
    private static final long serialVersionUID = -5263382542729729149L;

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

    public void addStyleBuilder(final RectangleRangedStyleBuilder rectangleRangedStyleBuilder) {
        this.styleBuilders.add(rectangleRangedStyleBuilder);
    }
}
