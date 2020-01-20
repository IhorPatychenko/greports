package styles.stylebuilders;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import positioning.VerticalRange;
import styles.VerticalRangedStyle;

import java.awt.*;

public class VerticalRangedStylesBuilder extends ReportStylesBuilder<VerticalRangedStyle, VerticalRange> {

    public VerticalRangedStylesBuilder(StylePriority priority) {
        super(priority);
    }

    @Override
    public VerticalRangedStylesBuilder setForegroundColor(Color foregroundColor) {
        super.setForegroundColor(foregroundColor);
        return this;
    }

    @Override
    public VerticalRangedStylesBuilder setFontColor(Color fontColor) {
        super.setFontColor(fontColor);
        return this;
    }

    @Override
    public VerticalRangedStylesBuilder setFillPattern(FillPatternType fillPattern) {
        super.setFillPattern(fillPattern);
        return this;
    }

    @Override
    public VerticalRangedStylesBuilder setBoldFont(Boolean boldFont) {
        super.setBoldFont(boldFont);
        return this;
    }

    @Override
    public VerticalRangedStylesBuilder setItalicFont(Boolean italicFont) {
        super.setItalicFont(italicFont);
        return this;
    }

    @Override
    public VerticalRangedStylesBuilder setUnderlineFont(FontUnderline underlineFont) {
        super.setUnderlineFont(underlineFont);
        return this;
    }

    @Override
    public VerticalRangedStylesBuilder setStrikeoutFont(Boolean strikeoutFont) {
        super.setStrikeoutFont(strikeoutFont);
        return this;
    }

    @Override
    public VerticalRangedStylesBuilder setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        super.setHorizontalAlignment(horizontalAlignment);
        return this;
    }

    @Override
    public VerticalRangedStylesBuilder setVerticalAlignment(VerticalAlignment verticalAlignment) {
        super.setVerticalAlignment(verticalAlignment);
        return this;
    }

    @Override
    public VerticalRangedStylesBuilder setBorderTop(BorderStyle borderTop) {
        super.setBorderTop(borderTop);
        return this;
    }

    @Override
    public VerticalRangedStylesBuilder setBorderRight(BorderStyle borderRight) {
        super.setBorderRight(borderRight);
        return this;
    }

    @Override
    public VerticalRangedStylesBuilder setBorderBottom(BorderStyle borderBottom) {
        super.setBorderBottom(borderBottom);
        return this;
    }

    @Override
    public VerticalRangedStylesBuilder setBorderLeft(BorderStyle borderLeft) {
        super.setBorderLeft(borderLeft);
        return this;
    }

    @Override
    public VerticalRangedStylesBuilder setBorder(BorderStyle border) {
        super.setBorder(border);
        return this;
    }

    @Override
    public VerticalRangedStylesBuilder setBorderColor(Color color) {
        super.setBorderColor(color);
        return this;
    }


    @Override
    public VerticalRangedStylesBuilder newStyle(VerticalRange tuple) {
        return newStyle(tuple, false);
    }

    public VerticalRangedStylesBuilder newStyle(VerticalRange tuple, boolean clonePreviousStyle){
        super.styleBuilder = newStyleBuilder(tuple, clonePreviousStyle);
        super.styleBuilders.add(styleBuilder);
        return this;
    }

    @Override
    protected VerticalRangedStyleBuilder newStyleBuilder(VerticalRange tuple, boolean clonePreviousStyle) {
        return null;
    }
}
