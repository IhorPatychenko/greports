package org.greports.styles.stylesbuilders;

import org.greports.positioning.HorizontalRange;
import org.greports.styles.HorizontalRangedStyle;

import java.io.Serializable;

public class HorizontalRangedStylesBuilder extends AbstractReportStylesBuilder<HorizontalRangedStylesBuilder, HorizontalRange, HorizontalRangedStyleBuilder, HorizontalRangedStyle> implements Serializable {
    private static final long serialVersionUID = 8715531976758425995L;

    public HorizontalRangedStylesBuilder(final StylePriority priority) {
        super(priority);
    }

    public HorizontalRangedStylesBuilder setColumnWidth(final Integer columnWidth) {
        this.styleBuilder.setColumnWidth(columnWidth);
        return this;
    }

    public HorizontalRangedStylesBuilder setWrapText(final Boolean wrapText){
        this.styleBuilder.wrapText = wrapText;
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
