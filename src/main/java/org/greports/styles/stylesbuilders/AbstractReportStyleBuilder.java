package org.greports.styles.stylesbuilders;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.greports.styles.ReportStyle;

import java.awt.*;
import java.io.Serializable;

public abstract class AbstractReportStyleBuilder<T extends ReportStyle<E>, E extends Serializable, R extends AbstractReportStyleBuilder<T, E, R>> implements Serializable {

    private static final long serialVersionUID = 7168319249010545243L;
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
    private Color leftBorderColor;
    private Color rightBorderColor;
    private Color topBorderColor;
    private Color bottomBorderColor;
    private Boolean hidden;
    private Short indentation;
    private Boolean locked;
    private Boolean quotePrefixed;
    private Short rotation;
    private Boolean shrinkToFit;
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

    public R setLeftBorderColor(Color color) {
        this.leftBorderColor = color;
        return getThis();
    }

    public R setRightBorderColor(Color color) {
        this.rightBorderColor = color;
        return getThis();
    }

    public R setTopBorderColor(Color color) {
        this.topBorderColor = color;
        return getThis();
    }

    public R setBottomBorderColor(Color color) {
        this.bottomBorderColor = color;
        return getThis();
    }

    public R setHidden(Boolean hidden) {
        this.hidden = hidden;
        return getThis();
    }

    public R setIndentation(Short indentation) {
        this.indentation = indentation;
        return getThis();
    }

    public R setLocked(Boolean locked) {
        this.locked = locked;
        return getThis();
    }

    public R setQuotePrefixed(Boolean quotePrefixed) {
        this.quotePrefixed = quotePrefixed;
        return getThis();
    }

    public R setRotation(Short rotation) {
        this.rotation = rotation;
        return getThis();
    }

    public R setShrinkToFit(Boolean shrinkToFit) {
        this.shrinkToFit = shrinkToFit;
        return getThis();
    }

    public R setBorderColor(Color color) {
        this.borderColor = color;
        return getThis();
    }

    public E getTuple() {
        return this.tuple;
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
            .setHidden(hidden)
            .setIndentation(indentation)
            .setLocked(locked)
            .setQuotePrefixed(quotePrefixed)
            .setRotation(rotation)
            .setShrinkToFit(shrinkToFit)
            .setLeftBorderColor(leftBorderColor)
            .setRightBorderColor(rightBorderColor)
            .setTopBorderColor(topBorderColor)
            .setBottomBorderColor(bottomBorderColor)
            .setBorderColor(borderColor);
        return setCustomStyles(reportStyle);
    }

    protected abstract R getThis();
    protected abstract T newStyleInstance();
    protected abstract T setCustomStyles(ReportStyle<E> style);
}