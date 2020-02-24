package org.greports.styles.stylesbuilders;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.greports.styles.ReportStyle;

import java.awt.Color;

abstract class AbstractReportStyleBuilder<T extends ReportStyle<E>, E, R extends AbstractReportStyleBuilder<T, E, R>> {

    protected Color foregroundColor;
    protected Color fontColor;
    protected Short fontSize;
    protected FillPatternType fillPattern = FillPatternType.SOLID_FOREGROUND;
    protected Boolean boldFont;
    protected Boolean italicFont;
    protected FontUnderline underlineFont;
    protected Boolean strikeoutFont;
    protected HorizontalAlignment horizontalAlignment;
    protected VerticalAlignment verticalAlignment;
    protected BorderStyle borderTop;
    protected BorderStyle borderRight;
    protected BorderStyle borderBottom;
    protected BorderStyle borderLeft;
    protected Color borderColor;
    protected E tuple;
    protected final boolean clonePreviousStyle;

    AbstractReportStyleBuilder(E tuple, boolean clonePreviousStyle) {
        this.tuple = tuple;
        this.clonePreviousStyle = clonePreviousStyle;
    }

    public void setTuple(E tuple) {
        this.tuple = tuple;
    }

    public R setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
        return getThis();
    }

    public R setFontColor(Color fontColor) {
        this.fontColor = fontColor;
        return getThis();
    }

    public R setFontSize(Short fontSize) {
        this.fontSize = fontSize;
        return getThis();
    }

    public R setFillPattern(FillPatternType fillPattern) {
        this.fillPattern = fillPattern;
        return getThis();
    }

    public R setBoldFont(Boolean boldFont) {
        this.boldFont = boldFont;
        return getThis();
    }

    public R setItalicFont(Boolean italicFont) {
        this.italicFont = italicFont;
        return getThis();
    }

    public R setUnderlineFont(FontUnderline underlineFont) {
        this.underlineFont = underlineFont;
        return getThis();
    }

    public R setStrikeoutFont(Boolean strikeoutFont) {
        this.strikeoutFont = strikeoutFont;
        return getThis();
    }

    public R setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        return getThis();
    }

    public R setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        return getThis();
    }

    public R setBorder(BorderStyle border) {
        setBorderTop(border);
        setBorderBottom(border);
        setBorderLeft(border);
        setBorderRight(border);
        return getThis();
    }

    public R setBorderTop(BorderStyle borderTop) {
        this.borderTop = borderTop;
        return getThis();
    }

    public R setBorderRight(BorderStyle borderRight) {
        this.borderRight = borderRight;
        return getThis();
    }

    public R setBorderBottom(BorderStyle borderBottom) {
        this.borderBottom = borderBottom;
        return getThis();
    }

    public R setBorderLeft(BorderStyle borderLeft) {
        this.borderLeft = borderLeft;
        return getThis();
    }

    public R setBorderColor(Color color) {
        this.borderColor = color;
        return getThis();
    }

    protected T buildStyle() {
        ReportStyle<E> reportStyle = newStyleInstance()
            .setClonePreviousStyle(clonePreviousStyle)
            .setForegroundColor(foregroundColor)
            .setFontColor(fontColor)
            .setFontSize(fontSize)
            .setFillPattern(fillPattern)
            .setBoldFont(boldFont)
            .setItalicFont(italicFont)
            .setUnderlineFont(underlineFont)
            .setStrikeoutFont(strikeoutFont)
            .setHorizontalAlignment(horizontalAlignment)
            .setVerticalAlignment(verticalAlignment)
            .setBorderTop(borderTop)
            .setBorderBottom(borderBottom)
            .setBorderLeft(borderLeft)
            .setBorderRight(borderRight)
            .setBorderColor(borderColor);
        return setCustomStyles(reportStyle);
    }

    protected abstract R getThis();
    protected abstract T newStyleInstance();
    protected abstract T setCustomStyles(ReportStyle<E> style);
}