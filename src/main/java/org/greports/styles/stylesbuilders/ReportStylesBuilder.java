package org.greports.styles.stylesbuilders;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.greports.positioning.HorizontalRange;
import org.greports.positioning.Position;
import org.greports.positioning.RectangleRange;
import org.greports.positioning.VerticalRange;
import org.greports.styles.Style;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReportStylesBuilder implements Serializable {
    private static final long serialVersionUID = -5263382542729729149L;

    protected ReportStyleBuilder<RectangleRange> getStyleBuilder(final RectangleRange rectangleRange, final boolean clonePreviousStyle) {
        return new ReportStyleBuilder<>(rectangleRange, clonePreviousStyle);
    }

    private final List<ReportStyleBuilder<RectangleRange>> styleBuilders = new ArrayList<>();
    private ReportStyleBuilder<RectangleRange> styleBuilder;

    public ReportStylesBuilder setFontName(String fontName) {
        this.styleBuilder.setFontName(fontName);
        return this;
    }

    public ReportStylesBuilder setForegroundColor(Color foregroundColor) {
        this.styleBuilder.setForegroundColor(foregroundColor);
        return this;
    }

    public ReportStylesBuilder setFontSize(Short fontSize) {
        this.styleBuilder.setFontSize(fontSize);
        return this;
    }

    public ReportStylesBuilder setFontColor(Color fontColor) {
        this.styleBuilder.setFontColor(fontColor);
        return this;
    }

    public ReportStylesBuilder setFillPattern(FillPatternType fillPattern) {
        this.styleBuilder.setFillPattern(fillPattern);
        return this;
    }

    public ReportStylesBuilder setBoldFont(Boolean boldFont) {
        this.styleBuilder.setBoldFont(boldFont);
        return this;
    }

    public ReportStylesBuilder setItalicFont(Boolean italicFont) {
        this.styleBuilder.setItalicFont(italicFont);
        return this;
    }

    public ReportStylesBuilder setUnderlineFont(FontUnderline underlineFont) {
        this.styleBuilder.setUnderlineFont(underlineFont);
        return this;
    }

    public ReportStylesBuilder setStrikeoutFont(Boolean strikeoutFont) {
        this.styleBuilder.setStrikeoutFont(strikeoutFont);
        return this;
    }

    public ReportStylesBuilder setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.styleBuilder.setHorizontalAlignment(horizontalAlignment);
        return this;
    }

    public ReportStylesBuilder setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.styleBuilder.setVerticalAlignment(verticalAlignment);
        return this;
    }

    public ReportStylesBuilder setBorderTop(BorderStyle borderTop) {
        this.styleBuilder.setBorderTop(borderTop);
        return this;
    }

    public ReportStylesBuilder setBorderRight(BorderStyle borderRight) {
        this.styleBuilder.setBorderRight(borderRight);
        return this;
    }

    public ReportStylesBuilder setBorderBottom(BorderStyle borderBottom) {
        this.styleBuilder.setBorderBottom(borderBottom);
        return this;
    }

    public ReportStylesBuilder setBorderLeft(BorderStyle borderLeft) {
        this.styleBuilder.setBorderLeft(borderLeft);
        return this;
    }

    public ReportStylesBuilder setBorder(BorderStyle border) {
        this.styleBuilder.setBorder(border);
        return this;
    }

    public ReportStylesBuilder setLeftBorderColor(Color color) {
        this.styleBuilder.setLeftBorderColor(color);
        return this;
    }

    public ReportStylesBuilder setRightBorderColor(Color color) {
        this.styleBuilder.setRightBorderColor(color);
        return this;
    }

    public ReportStylesBuilder setTopBorderColor(Color color) {
        this.styleBuilder.setTopBorderColor(color);
        return this;
    }

    public ReportStylesBuilder setBottomBorderColor(Color color) {
        this.styleBuilder.setBottomBorderColor(color);
        return this;
    }

    public ReportStylesBuilder setBorderColor(Color color) {
        this.styleBuilder.setBorderColor(color);
        return this;
    }

    public ReportStylesBuilder setHidden(Boolean hidden) {
        this.styleBuilder.setHidden(hidden);
        return this;
    }

    public ReportStylesBuilder setIndentation(Short indentation) {
        this.styleBuilder.setIndentation(indentation);
        return this;
    }

    public ReportStylesBuilder setLocked(Boolean locked) {
        this.styleBuilder.setLocked(locked);
        return this;
    }

    public ReportStylesBuilder setQuotePrefixed(Boolean quotePrefixed) {
        this.styleBuilder.setQuotePrefixed(quotePrefixed);
        return this;
    }

    public ReportStylesBuilder setRotation(Short rotation) {
        this.styleBuilder.setRotation(rotation);
        return this;
    }

    public ReportStylesBuilder setShrinkToFit(Boolean shrinkToFit) {
        this.styleBuilder.setShrinkToFit(shrinkToFit);
        return this;
    }

    public ReportStylesBuilder setRowHeight(Float rowHeight) {
        this.styleBuilder.setRowHeight(rowHeight);
        return this;
    }

    public ReportStylesBuilder setWrapText(Boolean wrapText) {
        this.styleBuilder.setWrapText(wrapText);
        return this;
    }

    public ReportStylesBuilder setColumnWidth(Integer columnWidth) {
        this.styleBuilder.setColumnWidth(columnWidth);
        return this;
    }

    public ReportStylesBuilder newStyle(final HorizontalRange horizontalRange, final boolean clonePreviousStyle) {
        return this.newStyle(horizontalRange.toRectangleRange(), clonePreviousStyle);
    }

    public ReportStylesBuilder newStyle(final VerticalRange verticalRange, final boolean clonePreviousStyle) {
        return this.newStyle(verticalRange.toRectangleRange(), clonePreviousStyle);
    }

    public ReportStylesBuilder newStyle(final Position position, final boolean clonePreviousStyle) {
        return this.newStyle(position.toRectangleRange(), clonePreviousStyle);
    }

    public ReportStylesBuilder newStyle(final RectangleRange rectangleRange, final boolean clonePreviousStyle) {
        this.styleBuilder = getStyleBuilder(rectangleRange, clonePreviousStyle);
        this.styleBuilders.add(styleBuilder);
        return this;
    }

    public List<ReportStyleBuilder<RectangleRange>> getStylesBuilders() {
        return this.styleBuilders;
    }

    public List<Style> getStyles(){
        return this.styleBuilders.stream().map(ReportStyleBuilder::buildStyle).collect(Collectors.toList());
    }

    public void mergeStyles(final ReportStylesBuilder other) {
        if (other != null) {
            styleBuilders.addAll(other.getStylesBuilders());
        }
    }

    public void addStyleBuilder(final ReportStyleBuilder<RectangleRange> reportStyleBuilder) {
        this.styleBuilders.add(reportStyleBuilder);
    }
}
