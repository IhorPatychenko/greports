package styles.stylebuilders;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

public abstract class ReportStylesBuilder<T, E> {

    public enum StylePriority {
        PRIORITY1, PRIORITY2, PRIORITY3, PRIORITY4
    }

    private StylePriority priority;
    protected Collection<ReportStyleBuilder<T, E>> styleBuilders = new ArrayList<>();
    protected ReportStyleBuilder<T, E> styleBuilder;

    public ReportStylesBuilder(StylePriority priority) {
        this.priority = priority;
    }

    public ReportStylesBuilder<T, E> setForegroundColor(Color foregroundColor){
        this.styleBuilder.setForegroundColor(foregroundColor);
        return this;
    }

    public ReportStylesBuilder<T, E> setFontColor(Color fontColor) {
        this.styleBuilder.setFontColor(fontColor);
        return this;
    }

    public ReportStylesBuilder<T, E> setFillPattern(FillPatternType fillPattern) {
        this.styleBuilder.setFillPattern(fillPattern);
        return this;
    }

    public ReportStylesBuilder<T, E> setBoldFont(Boolean boldFont) {
        this.styleBuilder.setBoldFont(boldFont);
        return this;
    }

    public ReportStylesBuilder<T, E> setItalicFont(Boolean italicFont) {
        this.styleBuilder.setItalicFont(italicFont);
        return this;
    }

    public ReportStylesBuilder<T, E> setUnderlineFont(FontUnderline underlineFont) {
        this.styleBuilder.setUnderlineFont(underlineFont);
        return this;
    }

    public ReportStylesBuilder<T, E> setStrikeoutFont(Boolean strikeoutFont) {
        this.styleBuilder.setStrikeoutFont(strikeoutFont);
        return this;
    }

    public ReportStylesBuilder<T, E> setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.styleBuilder.setHorizontalAlignment(horizontalAlignment);
        return this;
    }

    public ReportStylesBuilder<T, E> setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.styleBuilder.setVerticalAlignment(verticalAlignment);
        return this;
    }

    public ReportStylesBuilder<T, E> setBorderTop(BorderStyle borderTop) {
        this.styleBuilder.setBorderTop(borderTop);
        return this;
    }

    public ReportStylesBuilder<T, E> setBorderRight(BorderStyle borderRight) {
        this.styleBuilder.setBorderRight(borderRight);
        return this;
    }

    public ReportStylesBuilder<T, E> setBorderBottom(BorderStyle borderBottom) {
        this.styleBuilder.setBorderBottom(borderBottom);
        return this;
    }

    public ReportStylesBuilder<T, E> setBorderLeft(BorderStyle borderLeft) {
        this.styleBuilder.setBorderLeft(borderLeft);
        return this;
    }

    public ReportStylesBuilder<T, E> setBorder(BorderStyle border) {
        return setBorderTop(border)
                .setBorderBottom(border)
                .setBorderRight(border)
                .setBorderLeft(border);
    }

    public ReportStylesBuilder<T, E> setBorderColor(Color color) {
        this.styleBuilder.setBorderColor(color);
        return this;
    }

    public ReportStylesBuilder<T, E> newStyle(E tuple){
        return newStyle(tuple, false);
    }

    public ReportStylesBuilder<T, E> newStyle(E tuple, boolean clonePreviousStyle){
        styleBuilder = newStyleBuilder(tuple, clonePreviousStyle);
        styleBuilders.add(styleBuilder);
        return this;
    }

    protected abstract ReportStyleBuilder<T, E> newStyleBuilder(E tuple, boolean clonePreviousStyle);

    public Collection<ReportStyleBuilder<T, E>> getStylesBuilders() {
        return styleBuilders;
    }

    public StylePriority getPriority() {
        return priority;
    }

    public void mergeStyles(ReportStylesBuilder<T, E> other) {
        if (other != null) {
            styleBuilders.addAll(other.getStylesBuilders());
        }
    }

}
