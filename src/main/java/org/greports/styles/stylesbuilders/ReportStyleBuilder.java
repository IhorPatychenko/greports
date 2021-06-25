package org.greports.styles.stylesbuilders;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.greports.positioning.HorizontalRange;
import org.greports.positioning.Position;
import org.greports.positioning.Range;
import org.greports.positioning.RectangleRange;
import org.greports.positioning.VerticalRange;
import org.greports.styles.Style;

import java.awt.*;
import java.io.Serializable;

/**
 * A style builder for a single rows and columns range.
 * @param <E> a Range instance.
 * @see Range
 */
public class ReportStyleBuilder<E extends Range<?, ?>> implements Serializable {
    private static final long serialVersionUID = 7168319249010545243L;
    private String fontName;
    private Color foregroundColor;
    private Color fontColor;
    private Short fontSize;
    private FillPatternType fillPattern = FillPatternType.SOLID_FOREGROUND;
    private Boolean boldFont;
    private Boolean italicFont;
    private FontUnderline underlineFont;
    private Boolean strikeoutFont;
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;
    private BorderStyle borderTop;
    private BorderStyle borderRight;
    private BorderStyle borderBottom;
    private BorderStyle borderLeft;
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
    private Boolean wrapText;
    private Integer columnWidth;
    private Color borderColor;
    private RectangleRange range;
    private final boolean clonePreviousStyle;

    public ReportStyleBuilder(boolean clonePreviousStyle) {
        this((RectangleRange) null, clonePreviousStyle);
    }

    public ReportStyleBuilder(RectangleRange range, boolean clonePreviousStyle) {
        this.range = range;
        this.clonePreviousStyle = clonePreviousStyle;
    }

    public ReportStyleBuilder(HorizontalRange range, boolean clonePreviousStyle) {
        this(range.toRectangleRange(), clonePreviousStyle);
    }

    public ReportStyleBuilder(VerticalRange range, boolean clonePreviousStyle) {
        this(range.toRectangleRange(), clonePreviousStyle);
    }

    public ReportStyleBuilder(Position range, boolean clonePreviousStyle) {
        this(range.toRectangleRange(), clonePreviousStyle);
    }

    public ReportStyleBuilder(RectangleRange range, ReportStyleBuilder<?> styleBuilder) {
        this(range, styleBuilder.clonePreviousStyle);
        this.foregroundColor = styleBuilder.foregroundColor;
        this.fontColor = styleBuilder.fontColor;
        this.fontSize = styleBuilder.fontSize;
        this.fillPattern = styleBuilder.fillPattern;
        this.boldFont = styleBuilder.boldFont;
        this.italicFont = styleBuilder.italicFont;
        this.underlineFont = styleBuilder.underlineFont;
        this.strikeoutFont = styleBuilder.strikeoutFont;
        this.horizontalAlignment = styleBuilder.horizontalAlignment;
        this.verticalAlignment = styleBuilder.verticalAlignment;
        this.borderTop = styleBuilder.borderTop;
        this.borderRight = styleBuilder.borderRight;
        this.borderBottom = styleBuilder.borderBottom;
        this.borderLeft = styleBuilder.borderLeft;
        this.leftBorderColor = styleBuilder.leftBorderColor;
        this.rightBorderColor = styleBuilder.rightBorderColor;
        this.topBorderColor = styleBuilder.topBorderColor;
        this.bottomBorderColor = styleBuilder.bottomBorderColor;
        this.hidden = styleBuilder.hidden;
        this.indentation = styleBuilder.indentation;
        this.locked = styleBuilder.locked;
        this.quotePrefixed = styleBuilder.quotePrefixed;
        this.rotation = styleBuilder.rotation;
        this.shrinkToFit = styleBuilder.shrinkToFit;
        this.rowHeight = styleBuilder.rowHeight;
        this.wrapText = styleBuilder.wrapText;
        this.columnWidth = styleBuilder.columnWidth;
        this.borderColor = styleBuilder.borderColor;
    }

    public ReportStyleBuilder(HorizontalRange range, ReportStyleBuilder<?> styleBuilder) {
        this(range.toRectangleRange(), styleBuilder);
    }

    public ReportStyleBuilder(VerticalRange range, ReportStyleBuilder<?> styleBuilder) {
        this(range.toRectangleRange(), styleBuilder);
    }

    public ReportStyleBuilder(Position position, ReportStyleBuilder<?> styleBuilder) {
        this(position.toRectangleRange(), styleBuilder);
    }

    public void setRange(RectangleRange range) {
        this.range = range;
    }

    public ReportStyleBuilder<E> setFontName(String fontName) {
        this.fontName = fontName;
        return this;
    }

    public ReportStyleBuilder<E> setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }

    public ReportStyleBuilder<E> setFontColor(Color fontColor) {
        this.fontColor = fontColor;
        return this;
    }

    public ReportStyleBuilder<E> setFontSize(Short fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public ReportStyleBuilder<E> setFillPattern(FillPatternType fillPattern) {
        this.fillPattern = fillPattern;
        return this;
    }

    public ReportStyleBuilder<E> setBoldFont(Boolean boldFont) {
        this.boldFont = boldFont;
        return this;
    }

    public ReportStyleBuilder<E> setItalicFont(Boolean italicFont) {
        this.italicFont = italicFont;
        return this;
    }

    public ReportStyleBuilder<E> setUnderlineFont(FontUnderline underlineFont) {
        this.underlineFont = underlineFont;
        return this;
    }

    public ReportStyleBuilder<E> setStrikeoutFont(Boolean strikeoutFont) {
        this.strikeoutFont = strikeoutFont;
        return this;
    }

    public ReportStyleBuilder<E> setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    public ReportStyleBuilder<E> setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        return this;
    }

    public ReportStyleBuilder<E> setBorder(BorderStyle border) {
        setBorderTop(border);
        setBorderBottom(border);
        setBorderLeft(border);
        setBorderRight(border);
        return this;
    }

    public ReportStyleBuilder<E> setBorderTop(BorderStyle borderTop) {
        this.borderTop = borderTop;
        return this;
    }

    public ReportStyleBuilder<E> setBorderRight(BorderStyle borderRight) {
        this.borderRight = borderRight;
        return this;
    }

    public ReportStyleBuilder<E> setBorderBottom(BorderStyle borderBottom) {
        this.borderBottom = borderBottom;
        return this;
    }

    public ReportStyleBuilder<E> setBorderLeft(BorderStyle borderLeft) {
        this.borderLeft = borderLeft;
        return this;
    }

    public ReportStyleBuilder<E> setLeftBorderColor(Color color) {
        this.leftBorderColor = color;
        return this;
    }

    public ReportStyleBuilder<E> setRightBorderColor(Color color) {
        this.rightBorderColor = color;
        return this;
    }

    public ReportStyleBuilder<E> setTopBorderColor(Color color) {
        this.topBorderColor = color;
        return this;
    }

    public ReportStyleBuilder<E> setBottomBorderColor(Color color) {
        this.bottomBorderColor = color;
        return this;
    }

    public ReportStyleBuilder<E> setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public ReportStyleBuilder<E> setIndentation(Short indentation) {
        this.indentation = indentation;
        return this;
    }

    public ReportStyleBuilder<E> setLocked(Boolean locked) {
        this.locked = locked;
        return this;
    }

    public ReportStyleBuilder<E> setQuotePrefixed(Boolean quotePrefixed) {
        this.quotePrefixed = quotePrefixed;
        return this;
    }

    public ReportStyleBuilder<E> setRotation(Short rotation) {
        this.rotation = rotation;
        return this;
    }

    public ReportStyleBuilder<E> setShrinkToFit(Boolean shrinkToFit) {
        this.shrinkToFit = shrinkToFit;
        return this;
    }

    public ReportStyleBuilder<E> setRowHeight(Float rowHeight) {
        this.rowHeight = rowHeight;
        return this;
    }

    public ReportStyleBuilder<E> setColumnWidth(Integer columnWidth) {
        this.columnWidth = columnWidth;
        return this;
    }

    public ReportStyleBuilder<E> setBorderColor(Color color) {
        this.borderColor = color;
        return this;
    }

    public ReportStyleBuilder<E> setWrapText(Boolean wrapText) {
        this.wrapText = wrapText;
        return this;
    }

    public RectangleRange getRange() {
        return this.range;
    }

    protected Style buildStyle() {

        Style style = Style.builder()
                .range(range)
                .clonePreviousStyle(clonePreviousStyle)
                .fontName(fontName)
                .foregroundColor(foregroundColor)
                .fontColor(fontColor)
                .fontSize(fontSize)
                .fillPattern(fillPattern)
                .boldFont(boldFont)
                .italicFont(italicFont)
                .underlineFont(underlineFont)
                .strikeoutFont(strikeoutFont)
                .horizontalAlignment(horizontalAlignment)
                .verticalAlignment(verticalAlignment)
                .borderTop(borderTop)
                .borderBottom(borderBottom)
                .borderLeft(borderLeft)
                .borderRight(borderRight)
                .hidden(hidden)
                .indentation(indentation)
                .locked(locked)
                .quotePrefixed(quotePrefixed)
                .rotation(rotation)
                .shrinkToFit(shrinkToFit)
                .rowHeight(rowHeight)
                .wrapText(wrapText)
                .columnWidth(columnWidth)
                .leftBorderColor(leftBorderColor)
                .rightBorderColor(rightBorderColor)
                .topBorderColor(topBorderColor)
                .bottomBorderColor(bottomBorderColor)
                .borderColor(borderColor)
                .build();
        return setCustomStyles(style);
    }

    public ReportStyleBuilder<RectangleRange> toRectangeRangeStyleBuilder() {
        return new ReportStyleBuilder<>(this.range, this);
    }

    protected Style setCustomStyles(final Style style) {
        return style;
    }
}
