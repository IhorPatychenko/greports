package org.greports.styles.stylesbuilders;

import org.greports.positioning.HorizontalRange;
import org.greports.styles.HorizontalRangedStyle;

public class HorizontalRangedStylesBuilder extends AbstractReportStylesBuilder<HorizontalRangedStylesBuilder, HorizontalRange, HorizontalRangedStyleBuilder, HorizontalRangedStyle> {

    public HorizontalRangedStylesBuilder(final StylePriority priority) {
        super(priority);
    }

    public HorizontalRangedStylesBuilder setColumnWidth(Integer columnWidth) {
        this.styleBuilder.setColumnWidth(columnWidth);
        return this;
    }

    @Override
    protected HorizontalRangedStylesBuilder getThis() {
        return this;
    }

    @Override
    protected HorizontalRangedStyleBuilder getStyleBuilder(final HorizontalRange tuple, final boolean clonePreviousStyle) {
        return new HorizontalRangedStyleBuilder(tuple, clonePreviousStyle);
    }

    @Override
    public void mergeStyles(final HorizontalRangedStylesBuilder other) {
        if (other != null) {
            styleBuilders.addAll(other.getStylesBuilders());
        }
    }
}
