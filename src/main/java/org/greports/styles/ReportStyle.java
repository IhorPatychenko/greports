package org.greports.styles;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.greports.styles.interfaces.StripedRows.StripedRowsIndex;

import java.awt.*;
import java.io.Serializable;

public class ReportStyle<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 704161250828995451L;

    private final T range;
    private Color foregroundColor;
    private Color fontColor;
    private FillPatternType fillPattern;
    private Short fontSize;
    private Boolean boldFont;
    private Boolean italicFont;
    private FontUnderline underlineFont;
    private Boolean strikeoutFont;
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;
    private BorderStyle borderTop;
    private BorderStyle borderBottom;
    private BorderStyle borderLeft;
    private BorderStyle borderRight;
    private Color borderColor;
    private StripedRowsIndex stripedRowsIndex;
    private Color stripedRowsColor;
    private boolean clonePreviousStyle;
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

    protected ReportStyle(T tuple){
        this.range = tuple;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public ReportStyle<T> setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public ReportStyle<T> setFontColor(Color fontColor) {
        this.fontColor = fontColor;
        return this;
    }

    public FillPatternType getFillPattern() {
        return fillPattern;
    }

    public ReportStyle<T> setFillPattern(FillPatternType fillPattern) {
        this.fillPattern = fillPattern;
        return this;
    }

    public Short getFontSize() {
        return fontSize;
    }

    public ReportStyle<T> setFontSize(Short fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public Boolean getBoldFont() {
        return boldFont;
    }

    public ReportStyle<T> setBoldFont(Boolean boldFont) {
        this.boldFont = boldFont;
        return this;
    }

    public Boolean getItalicFont() {
        return italicFont;
    }

    public ReportStyle<T> setItalicFont(Boolean italicFont) {
        this.italicFont = italicFont;
        return this;
    }

    public FontUnderline getUnderlineFont() {
        return underlineFont;
    }

    public ReportStyle<T> setUnderlineFont(FontUnderline underlineFont) {
        this.underlineFont = underlineFont;
        return this;
    }

    public Boolean getStrikeoutFont() {
        return strikeoutFont;
    }

    public ReportStyle<T> setStrikeoutFont(Boolean strikeoutFont) {
        this.strikeoutFont = strikeoutFont;
        return this;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public ReportStyle<T> setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    public ReportStyle<T> setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        return this;
    }

    public BorderStyle getBorderTop() {
        return borderTop;
    }

    public ReportStyle<T> setBorderTop(BorderStyle borderTop) {
        this.borderTop = borderTop;
        return this;
    }

    public BorderStyle getBorderBottom() {
        return borderBottom;
    }

    public ReportStyle<T> setBorderBottom(BorderStyle borderBottom) {
        this.borderBottom = borderBottom;
        return this;
    }

    public BorderStyle getBorderLeft() {
        return borderLeft;
    }

    public ReportStyle<T> setBorderLeft(BorderStyle borderLeft) {
        this.borderLeft = borderLeft;
        return this;
    }

    public BorderStyle getBorderRight() {
        return borderRight;
    }

    public ReportStyle<T> setBorderRight(BorderStyle borderRight) {
        this.borderRight = borderRight;
        return this;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public ReportStyle<T> setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public StripedRowsIndex getStripedRowsIndex() {
        return stripedRowsIndex;
    }

    public ReportStyle<T> setStripedRowsIndex(StripedRowsIndex stripedRowsIndex) {
        this.stripedRowsIndex = stripedRowsIndex;
        return this;
    }

    public Color getStripedRowsColor() {
        return stripedRowsColor;
    }

    public ReportStyle<T> setStripedRowsColor(Color stripedRowsColor) {
        this.stripedRowsColor = stripedRowsColor;
        return this;
    }

    public boolean isClonePreviousStyle() {
        return clonePreviousStyle;
    }

    public ReportStyle<T> setClonePreviousStyle(boolean clonePreviousStyle) {
        this.clonePreviousStyle = clonePreviousStyle;
        return this;
    }

    public Color getLeftBorderColor() {
        return leftBorderColor;
    }

    public ReportStyle<T> setLeftBorderColor(Color leftBorderColor) {
        this.leftBorderColor = leftBorderColor;
        return this;
    }

    public Color getRightBorderColor() {
        return rightBorderColor;
    }

    public ReportStyle<T> setRightBorderColor(Color rightBorderColor) {
        this.rightBorderColor = rightBorderColor;
        return this;
    }

    public Color getTopBorderColor() {
        return topBorderColor;
    }

    public ReportStyle<T> setTopBorderColor(Color topBorderColor) {
        this.topBorderColor = topBorderColor;
        return this;
    }

    public Color getBottomBorderColor() {
        return bottomBorderColor;
    }

    public ReportStyle<T> setBottomBorderColor(Color bottomBorderColor) {
        this.bottomBorderColor = bottomBorderColor;
        return this;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public ReportStyle<T> setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public Short getIndentation() {
        return indentation;
    }

    public ReportStyle<T> setIndentation(Short indentation) {
        this.indentation = indentation;
        return this;
    }

    public Boolean getLocked() {
        return locked;
    }

    public ReportStyle<T> setLocked(Boolean locked) {
        this.locked = locked;
        return this;
    }

    public Boolean getQuotePrefixed() {
        return quotePrefixed;
    }

    public ReportStyle<T> setQuotePrefixed(Boolean quotePrefixed) {
        this.quotePrefixed = quotePrefixed;
        return this;
    }

    public Short getRotation() {
        return rotation;
    }

    public ReportStyle<T> setRotation(Short rotation) {
        this.rotation = rotation;
        return this;
    }

    public Boolean getShrinkToFit() {
        return shrinkToFit;
    }

    public ReportStyle<T> setShrinkToFit(Boolean shrinkToFit) {
        this.shrinkToFit = shrinkToFit;
        return this;
    }

    public T getRange() {
        return range;
    }
}
