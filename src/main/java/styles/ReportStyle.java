package styles;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

public class ReportStyle {
    private IndexedColors foregroundColor;
    private IndexedColors fontColor;
    private FillPatternType fillPattern;
    private Boolean boldFont;
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;
    private BorderStyle borderTop;
    private BorderStyle borderRight;
    private BorderStyle borderBottom;
    private BorderStyle borderLeft;

    ReportStyle(IndexedColors foregroundColor, IndexedColors fontColor, FillPatternType fillPattern, Boolean boldFont, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, BorderStyle borderTop, BorderStyle borderRight, BorderStyle borderBottom, BorderStyle borderLeft) {
        this.foregroundColor = foregroundColor;
        this.fontColor = fontColor;
        this.fillPattern = fillPattern;
        this.boldFont = boldFont;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.borderTop = borderTop;
        this.borderRight = borderRight;
        this.borderBottom = borderBottom;
        this.borderLeft = borderLeft;
    }

    public static <T extends ReportStyle> T from(ReportStyle rs, Class<T> clazz) {
        if(clazz.equals(HorizontalRangedStyle.class)){
            return clazz.cast(new HorizontalRangedStyle(rs.getForegroundColor(), rs.getFontColor(), rs.getFillPattern(), rs.isBoldFont(), rs.getHorizontalAlignment(), rs.getVerticalAlignment(), rs.getBorderTop(), rs.getBorderRight(), rs.getBorderBottom(), rs.getBorderLeft()));
        } else if(clazz.equals(VerticalRangedStyle.class)){
            return clazz.cast(new VerticalRangedStyle(rs.getForegroundColor(), rs.getFontColor(), rs.getFillPattern(), rs.isBoldFont(), rs.getHorizontalAlignment(), rs.getVerticalAlignment(), rs.getBorderTop(), rs.getBorderRight(), rs.getBorderBottom(), rs.getBorderLeft()));
        } else if(clazz.equals(PositionedStyle.class)){
            return clazz.cast(new PositionedStyle(rs.getForegroundColor(), rs.getFontColor(), rs.getFillPattern(), rs.isBoldFont(), rs.getHorizontalAlignment(), rs.getVerticalAlignment(), rs.getBorderTop(), rs.getBorderRight(), rs.getBorderBottom(), rs.getBorderLeft()));
        } else {
            return clazz.cast(new RectangleRangedStyle(rs.getForegroundColor(), rs.getFontColor(), rs.getFillPattern(), rs.isBoldFont(), rs.getHorizontalAlignment(), rs.getVerticalAlignment(), rs.getBorderTop(), rs.getBorderRight(), rs.getBorderBottom(), rs.getBorderLeft()));
        }
    }

    public IndexedColors getForegroundColor() {
        return foregroundColor;
    }

    public IndexedColors getFontColor() {
        return fontColor;
    }

    public FillPatternType getFillPattern() {
        return fillPattern;
    }

    public Boolean isBoldFont() {
        return boldFont;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    public BorderStyle getBorderTop() {
        return borderTop;
    }

    public BorderStyle getBorderRight() {
        return borderRight;
    }

    public BorderStyle getBorderBottom() {
        return borderBottom;
    }

    public BorderStyle getBorderLeft() {
        return borderLeft;
    }
}
