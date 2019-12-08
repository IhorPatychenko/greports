package styles;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import utils.HorizontalRange;
import utils.Position;
import utils.RectangleRange;
import utils.Tuple;
import utils.VerticalRange;

public class ReportStyleBuilder<T extends ReportStyle> {

    private Class<T> forClass;
    private ReportStylesBuilder<T> parent;

    private IndexedColors foregroundColor;
    private IndexedColors fontColor;
    private FillPatternType fillPattern = FillPatternType.SOLID_FOREGROUND;
    private Boolean boldFont;
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;
    private BorderStyle borderTop;
    private BorderStyle borderRight;
    private BorderStyle borderBottom;
    private BorderStyle borderLeft;
    private Tuple coodrinates;

    public ReportStyleBuilder(Class<T> forClass, ReportStylesBuilder<T> parent, Tuple tuple) {
        this.forClass = forClass;
        this.parent = parent;
        this.coodrinates = tuple;
    }

    public ReportStyleBuilder<T> setForegroundColor(IndexedColors foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }

    public ReportStyleBuilder<T> setFontColor(IndexedColors fontColor) {
        this.fontColor = fontColor;
        return this;
    }

    public ReportStyleBuilder<T> setFillPattern(FillPatternType fillPattern) {
        this.fillPattern = fillPattern;
        return this;
    }

    public ReportStyleBuilder<T> setBoldFont(Boolean boldFont) {
        this.boldFont = boldFont;
        return this;
    }

    public ReportStyleBuilder<T> setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    public ReportStyleBuilder<T> setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        return this;
    }

    public ReportStyleBuilder<T> setBorderTop(BorderStyle borderTop) {
        this.borderTop = borderTop;
        return this;
    }

    public ReportStyleBuilder<T> setBorderRight(BorderStyle borderRight) {
        this.borderRight = borderRight;
        return this;
    }

    public ReportStyleBuilder<T> setBorderBottom(BorderStyle borderBottom) {
        this.borderBottom = borderBottom;
        return this;
    }

    public ReportStyleBuilder<T> setBorderLeft(BorderStyle borderLeft) {
        this.borderLeft = borderLeft;
        return this;
    }

    public ReportStyleBuilder<T> setBorder(BorderStyle border) {
        return setBorderTop(border)
                .setBorderBottom(border)
                .setBorderLeft(border)
                .setBorderRight(border);
    }

    public ReportStyleBuilder<T> setCoodrinates(Tuple coodrinates) {
        this.coodrinates = coodrinates;
        return this;
    }

    public ReportStylesBuilder<T> parent() {
        return parent;
    }

    T build() {
        ReportStyle reportStyle = new ReportStyle()
                .setForegroundColor(foregroundColor)
                .setFontColor(fontColor)
                .setFillPattern(fillPattern)
                .setBoldFont(boldFont)
                .setHorizontalAlignment(horizontalAlignment)
                .setVerticalAlignment(verticalAlignment)
                .setBorderTop(borderTop)
                .setBorderBottom(borderBottom)
                .setBorderLeft(borderLeft)
                .setBorderRight(borderRight);
        if (forClass.equals(HorizontalRangedStyle.class)) {
            HorizontalRangedStyle hrs = new HorizontalRangedStyle(reportStyle);
            hrs.setRange((HorizontalRange) coodrinates);
            return forClass.cast(hrs);
        } else if (forClass.equals(VerticalRangedStyle.class)) {
            VerticalRangedStyle vrs = new VerticalRangedStyle(reportStyle);
            vrs.setRange((VerticalRange) coodrinates);
            return forClass.cast(vrs);
        } else if (forClass.equals(PositionedStyle.class)) {
            PositionedStyle ps = new PositionedStyle(reportStyle);
            ps.setPosition((Position) coodrinates);
            return forClass.cast(ps);
        } else if(forClass.equals(RectangleRangedStyle.class)){
            RectangleRangedStyle rrs = new RectangleRangedStyle(reportStyle);
            rrs.setRange((RectangleRange) coodrinates);
            return forClass.cast(rrs);
        }
        return null;
    }
}
