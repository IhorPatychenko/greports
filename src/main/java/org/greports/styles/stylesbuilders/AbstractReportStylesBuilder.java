package org.greports.styles.stylesbuilders;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.greports.styles.ReportStyle;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class AbstractReportStylesBuilder<T, E, R extends AbstractReportStyleBuilder<U, E, R>, U extends ReportStyle<E>> {

    public enum StylePriority {
        PRIORITY1, PRIORITY2, PRIORITY3, PRIORITY4
    }

    private final StylePriority priority;
    protected final Collection<R> styleBuilders = new ArrayList<>();
    protected R styleBuilder;

    public AbstractReportStylesBuilder(StylePriority priority) {
        this.priority = priority;
    }

    public StylePriority getPriority() {
        return priority;
    }

    public T setForegroundColor(Color foregroundColor) {
        this.styleBuilder.setForegroundColor(foregroundColor);
        return getThis();
    }

    public T setFontSize(Short fontSize) {
        this.styleBuilder.setFontSize(fontSize);
        return getThis();
    }

    public T setFontColor(Color fontColor) {
        this.styleBuilder.setFontColor(fontColor);
        return getThis();
    }

    public T setFillPattern(FillPatternType fillPattern) {
        this.styleBuilder.setFillPattern(fillPattern);
        return getThis();
    }

    public T setBoldFont(Boolean boldFont) {
        this.styleBuilder.setBoldFont(boldFont);
        return getThis();
    }

    public T setItalicFont(Boolean italicFont) {
        this.styleBuilder.setItalicFont(italicFont);
        return getThis();
    }

    public T setUnderlineFont(FontUnderline underlineFont) {
        this.styleBuilder.setUnderlineFont(underlineFont);
        return getThis();
    }

    public T setStrikeoutFont(Boolean strikeoutFont) {
        this.styleBuilder.setStrikeoutFont(strikeoutFont);
        return getThis();
    }

    public T setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.styleBuilder.setHorizontalAlignment(horizontalAlignment);
        return getThis();
    }

    public T setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.styleBuilder.setVerticalAlignment(verticalAlignment);
        return getThis();
    }

    public T setBorderTop(BorderStyle borderTop) {
        this.styleBuilder.setBorderTop(borderTop);
        return getThis();
    }

    public T setBorderRight(BorderStyle borderRight) {
        this.styleBuilder.setBorderRight(borderRight);
        return getThis();
    }

    public T setBorderBottom(BorderStyle borderBottom) {
        this.styleBuilder.setBorderBottom(borderBottom);
        return getThis();
    }

    public T setBorderLeft(BorderStyle borderLeft) {
        this.styleBuilder.setBorderLeft(borderLeft);
        return getThis();
    }

    public T setBorder(BorderStyle border) {
        this.styleBuilder.setBorder(border);
        return getThis();
    }

    public T setBorderColor(Color color) {
        this.styleBuilder.setBorderColor(color);
        return getThis();
    }

    public T newStyle(E tuple) {
        return newStyle(tuple, false);
    }

    public T newStyle(final E tuple, final boolean clonePreviousStyle) {
        this.styleBuilder = getStyleBuilder(tuple, clonePreviousStyle);
        this.styleBuilders.add(styleBuilder);
        return getThis();
    }

    public Collection<R> getStylesBuilders() {
        return this.styleBuilders;
    }

    public Collection<U> getStyles(){
        return this.styleBuilders.stream().map(AbstractReportStyleBuilder::buildStyle).collect(Collectors.toList());
    }

    public abstract void mergeStyles(T other);
    protected abstract T getThis();
    protected abstract R getStyleBuilder(E tuple, boolean clonePreviousStyle);
}
