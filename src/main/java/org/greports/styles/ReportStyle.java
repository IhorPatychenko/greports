package org.greports.styles;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.greports.positioning.RectangleRange;
import org.greports.styles.interfaces.StripedRows;

import java.awt.*;
import java.io.Serializable;

public class ReportStyle implements Serializable {
    private static final long serialVersionUID = 4183157194410162170L;

    private final RectangleRange range;
    private String fontName;
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
    private StripedRows.StripedRowsIndex stripedRowsIndex;
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
    private Float rowHeight;
    private Integer columnWidth;
    private Boolean wrapText;

    public ReportStyle(RectangleRange range){
        this.range = range;
    }

    public String getFontName() {
        return fontName;
    }

    public ReportStyle setFontName(String fontName) {
        this.fontName = fontName;
        return this;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public ReportStyle setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public ReportStyle setFontColor(Color fontColor) {
        this.fontColor = fontColor;
        return this;
    }

    public FillPatternType getFillPattern() {
        return fillPattern;
    }

    public ReportStyle setFillPattern(FillPatternType fillPattern) {
        this.fillPattern = fillPattern;
        return this;
    }

    public Short getFontSize() {
        return fontSize;
    }

    public ReportStyle setFontSize(Short fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public Boolean getBoldFont() {
        return boldFont;
    }

    public ReportStyle setBoldFont(Boolean boldFont) {
        this.boldFont = boldFont;
        return this;
    }

    public Boolean getItalicFont() {
        return italicFont;
    }

    public ReportStyle setItalicFont(Boolean italicFont) {
        this.italicFont = italicFont;
        return this;
    }

    public FontUnderline getUnderlineFont() {
        return underlineFont;
    }

    public ReportStyle setUnderlineFont(FontUnderline underlineFont) {
        this.underlineFont = underlineFont;
        return this;
    }

    public Boolean getStrikeoutFont() {
        return strikeoutFont;
    }

    public ReportStyle setStrikeoutFont(Boolean strikeoutFont) {
        this.strikeoutFont = strikeoutFont;
        return this;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public ReportStyle setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    public ReportStyle setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        return this;
    }

    public BorderStyle getBorderTop() {
        return borderTop;
    }

    public ReportStyle setBorderTop(BorderStyle borderTop) {
        this.borderTop = borderTop;
        return this;
    }

    public BorderStyle getBorderBottom() {
        return borderBottom;
    }

    public ReportStyle setBorderBottom(BorderStyle borderBottom) {
        this.borderBottom = borderBottom;
        return this;
    }

    public BorderStyle getBorderLeft() {
        return borderLeft;
    }

    public ReportStyle setBorderLeft(BorderStyle borderLeft) {
        this.borderLeft = borderLeft;
        return this;
    }

    public BorderStyle getBorderRight() {
        return borderRight;
    }

    public ReportStyle setBorderRight(BorderStyle borderRight) {
        this.borderRight = borderRight;
        return this;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public ReportStyle setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public StripedRows.StripedRowsIndex getStripedRowsIndex() {
        return stripedRowsIndex;
    }

    public ReportStyle setStripedRowsIndex(StripedRows.StripedRowsIndex stripedRowsIndex) {
        this.stripedRowsIndex = stripedRowsIndex;
        return this;
    }

    public Color getStripedRowsColor() {
        return stripedRowsColor;
    }

    public ReportStyle setStripedRowsColor(Color stripedRowsColor) {
        this.stripedRowsColor = stripedRowsColor;
        return this;
    }

    public boolean isClonePreviousStyle() {
        return clonePreviousStyle;
    }

    public ReportStyle setClonePreviousStyle(boolean clonePreviousStyle) {
        this.clonePreviousStyle = clonePreviousStyle;
        return this;
    }

    public Color getLeftBorderColor() {
        return leftBorderColor;
    }

    public ReportStyle setLeftBorderColor(Color leftBorderColor) {
        this.leftBorderColor = leftBorderColor;
        return this;
    }

    public Color getRightBorderColor() {
        return rightBorderColor;
    }

    public ReportStyle setRightBorderColor(Color rightBorderColor) {
        this.rightBorderColor = rightBorderColor;
        return this;
    }

    public Color getTopBorderColor() {
        return topBorderColor;
    }

    public ReportStyle setTopBorderColor(Color topBorderColor) {
        this.topBorderColor = topBorderColor;
        return this;
    }

    public Color getBottomBorderColor() {
        return bottomBorderColor;
    }

    public ReportStyle setBottomBorderColor(Color bottomBorderColor) {
        this.bottomBorderColor = bottomBorderColor;
        return this;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public ReportStyle setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public Short getIndentation() {
        return indentation;
    }

    public ReportStyle setIndentation(Short indentation) {
        this.indentation = indentation;
        return this;
    }

    public Boolean getLocked() {
        return locked;
    }

    public ReportStyle setLocked(Boolean locked) {
        this.locked = locked;
        return this;
    }

    public Boolean getQuotePrefixed() {
        return quotePrefixed;
    }

    public ReportStyle setQuotePrefixed(Boolean quotePrefixed) {
        this.quotePrefixed = quotePrefixed;
        return this;
    }

    public Short getRotation() {
        return rotation;
    }

    public ReportStyle setRotation(Short rotation) {
        this.rotation = rotation;
        return this;
    }

    public Boolean getShrinkToFit() {
        return shrinkToFit;
    }

    public ReportStyle setShrinkToFit(Boolean shrinkToFit) {
        this.shrinkToFit = shrinkToFit;
        return this;
    }

    public Float getRowHeight() {
        return rowHeight;
    }

    public ReportStyle setRowHeight(Float rowHeight) {
        this.rowHeight = rowHeight;
        return this;
    }

    public Boolean getWrapText() {
        return wrapText;
    }

    public ReportStyle setWrapText(Boolean wrapText) {
        this.wrapText = wrapText;
        return this;
    }

    public Integer getColumnWidth() {
        return columnWidth;
    }

    public ReportStyle setColumnWidth(Integer columnWidth) {
        this.columnWidth = columnWidth;
        return this;
    }

    public RectangleRange getRange() {
        return range;
    }

}
