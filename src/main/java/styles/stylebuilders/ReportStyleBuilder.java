package styles.stylebuilders;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.awt.Color;

abstract class ReportStyleBuilder<T, E> {

    private Color foregroundColor;
    private Color fontColor;
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
    private Color borderColor;
    private T tuple;
    private boolean clonePreviousStyle;

    ReportStyleBuilder(T tuple, boolean clonePreviousStyle) {
        this.tuple = tuple;
        this.clonePreviousStyle = clonePreviousStyle;
    }

    void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    void setFillPattern(FillPatternType fillPattern) {
        this.fillPattern = fillPattern;
    }

    void setBoldFont(Boolean boldFont) {
        this.boldFont = boldFont;
    }

    void setItalicFont(Boolean italicFont) {
        this.italicFont = italicFont;
    }

    void setUnderlineFont(FontUnderline underlineFont) {
        this.underlineFont = underlineFont;
    }

    void setStrikeoutFont(Boolean strikeoutFont) {
        this.strikeoutFont = strikeoutFont;
    }

    void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    void setBorderTop(BorderStyle borderTop) {
        this.borderTop = borderTop;
    }

    void setBorderRight(BorderStyle borderRight) {
        this.borderRight = borderRight;
    }

    void setBorderBottom(BorderStyle borderBottom) {
        this.borderBottom = borderBottom;
    }

    void setBorderLeft(BorderStyle borderLeft) {
        this.borderLeft = borderLeft;
    }

    void setBorderColor(Color color) {
        this.borderColor = color;
    }

    abstract E buildStyle();

//    E buildStyle() {
//            ReportStyle reportStyle = new ReportStyle()
//                    .setClonePreviousStyle(clonePreviousStyle)
//                    .setForegroundColor(foregroundColor)
//                    .setFontColor(fontColor)
//                    .setFillPattern(fillPattern)
//                    .setBoldFont(boldFont)
//                    .setItalicFont(italicFont)
//                    .setUnderlineFont(underlineFont)
//                    .setStrikeoutFont(strikeoutFont)
//                    .setHorizontalAlignment(horizontalAlignment)
//                    .setVerticalAlignment(verticalAlignment)
//                    .setBorderTop(borderTop)
//                    .setBorderBottom(borderBottom)
//                    .setBorderLeft(borderLeft)
//                    .setBorderRight(borderRight)
//                    .setBorderColor(borderColor);
//            if (forClass.equals(HorizontalRangedStyle.class)) {
//                HorizontalRangedStyle hrs = new HorizontalRangedStyle(reportStyle);
//                hrs.setRange((HorizontalRange) tuple);
//                return forClass.cast(hrs);
//            } else if (forClass.equals(VerticalRangedStyle.class)) {
//                VerticalRangedStyle vrs = new VerticalRangedStyle(reportStyle);
//                vrs.setRange((VerticalRange) tuple);
//                return forClass.cast(vrs);
//            } else if (forClass.equals(PositionedStyle.class)) {
//                PositionedStyle ps = new PositionedStyle(reportStyle);
//                ps.setPosition((Position) tuple);
//                return forClass.cast(ps);
//            } else if(forClass.equals(RectangleRangedStyle.class)){
//                RectangleRangedStyle rrs = new RectangleRangedStyle(reportStyle);
//                rrs.setRange((RectangleRange) tuple);
//                return forClass.cast(rrs);
//            }
//        return null;
//    }
}