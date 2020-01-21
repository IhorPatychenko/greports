package styles;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import styles.interfaces.StripedRows.StripedRowsIndex;

import java.awt.Color;

public class ReportStyle<T> {

    private T range;
    private Color foregroundColor;
    private Color fontColor;
    private FillPatternType fillPattern;
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

    protected ReportStyle(T tuple){
        this.range = tuple;
    }

    public boolean isClonePreviousStyle() {
        return clonePreviousStyle;
    }

    public ReportStyle<T> setClonePreviousStyle(final boolean clonePreviousStyle) {
        this.clonePreviousStyle = clonePreviousStyle;
        return this;
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

    public BorderStyle getBorderRight() {
        return borderRight;
    }

    public ReportStyle<T> setBorderRight(BorderStyle borderRight) {
        this.borderRight = borderRight;
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

    public Color getBorderColor() {
        return borderColor;
    }

    public ReportStyle<T> setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public T getRange() {
        return range;
    }
}
