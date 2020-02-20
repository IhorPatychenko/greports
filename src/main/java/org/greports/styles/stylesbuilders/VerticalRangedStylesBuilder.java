package org.greports.styles.stylesbuilders;

import org.greports.positioning.VerticalRange;
import org.greports.styles.VerticalRangedStyle;

public class VerticalRangedStylesBuilder extends AbstractReportStylesBuilder<VerticalRangedStylesBuilder, VerticalRange, VerticalRangedStyleBuilder, VerticalRangedStyle> {

    public VerticalRangedStylesBuilder(StylePriority priority) {
        super(priority);
    }

    public VerticalRangedStylesBuilder setRowHeight(Float rowHeight) {
        this.styleBuilder.setRowHeight(rowHeight);
        return this;
    }

    @Override
    protected VerticalRangedStylesBuilder getThis() {
        return this;
    }

    @Override
    protected VerticalRangedStyleBuilder getStyleBuilder(final VerticalRange tuple, final boolean clonePreviousStyle) {
        return new VerticalRangedStyleBuilder(tuple, clonePreviousStyle);
    }

    @Override
    public void mergeStyles(final VerticalRangedStylesBuilder other) {
        if (other != null) {
            styleBuilders.addAll(other.getStylesBuilders());
        }
    }
}
