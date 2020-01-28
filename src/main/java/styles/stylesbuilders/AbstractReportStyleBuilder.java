package styles.stylesbuilders;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import styles.ReportStyle;

import java.awt.Color;

abstract class AbstractReportStyleBuilder<T extends ReportStyle<E>, E> {

    protected Color foregroundColor;
    protected Color fontColor;
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
    protected final E tuple;
    protected final boolean clonePreviousStyle;

    AbstractReportStyleBuilder(E tuple, boolean clonePreviousStyle) {
        this.tuple = tuple;
        this.clonePreviousStyle = clonePreviousStyle;
    }

    protected void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    protected void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    protected void setFillPattern(FillPatternType fillPattern) {
        this.fillPattern = fillPattern;
    }

    protected void setBoldFont(Boolean boldFont) {
        this.boldFont = boldFont;
    }

    protected void setItalicFont(Boolean italicFont) {
        this.italicFont = italicFont;
    }

    protected void setUnderlineFont(FontUnderline underlineFont) {
        this.underlineFont = underlineFont;
    }

    protected void setStrikeoutFont(Boolean strikeoutFont) {
        this.strikeoutFont = strikeoutFont;
    }

    protected void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    protected void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    protected void setBorder(BorderStyle border) {
        setBorderTop(border);
        setBorderBottom(border);
        setBorderLeft(border);
        setBorderRight(border);
    }

    protected void setBorderTop(BorderStyle borderTop) {
        this.borderTop = borderTop;
    }

    protected void setBorderRight(BorderStyle borderRight) {
        this.borderRight = borderRight;
    }

    protected void setBorderBottom(BorderStyle borderBottom) {
        this.borderBottom = borderBottom;
    }

    protected void setBorderLeft(BorderStyle borderLeft) {
        this.borderLeft = borderLeft;
    }

    protected void setBorderColor(Color color) {
        this.borderColor = color;
    }

    protected T buildStyle() {
        ReportStyle<E> reportStyle = newStyleInstance()
            .setClonePreviousStyle(clonePreviousStyle)
            .setForegroundColor(foregroundColor)
            .setFontColor(fontColor)
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

    protected abstract T newStyleInstance();
    protected abstract T setCustomStyles(ReportStyle<E> style);
}