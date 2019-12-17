package styles;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import styles.interfaces.StripedRows.StripedRowsIndex;

import java.awt.*;

public class ReportStyle {

    private Color foregroundColor;
    private Color fontColor;
    private FillPatternType fillPattern;
    private Boolean boldFont;
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;
    private BorderStyle borderTop;
    private BorderStyle borderBottom;
    private BorderStyle borderLeft;
    private BorderStyle borderRight;
    private Color borderColor;
    private StripedRowsIndex stripedRowsIndex;
    private Color stripedRowsColor;

    protected ReportStyle(){}

    protected ReportStyle(ReportStyle rs) {
        this.foregroundColor = rs.getForegroundColor();
        this.fontColor = rs.getFontColor();
        this.fillPattern = rs.getFillPattern();
        this.boldFont = rs.getBoldFont();
        this.horizontalAlignment = rs.getHorizontalAlignment();
        this.verticalAlignment = rs.getVerticalAlignment();
        this.borderTop = rs.getBorderTop();
        this.borderBottom = rs.getBorderBottom();
        this.borderLeft = rs.getBorderLeft();
        this.borderRight = rs.getBorderRight();
        this.borderColor = rs.getBorderColor();
        this.stripedRowsIndex = rs.getStripedRowsIndex();
        this.stripedRowsColor = rs.getStripedRowsColor();
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

    public Boolean getBoldFont() {
        return boldFont;
    }

    public ReportStyle setBoldFont(Boolean boldFont) {
        this.boldFont = boldFont;
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

    public BorderStyle getBorderRight() {
        return borderRight;
    }

    public ReportStyle setBorderRight(BorderStyle borderRight) {
        this.borderRight = borderRight;
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

    public StripedRowsIndex getStripedRowsIndex() {
        return stripedRowsIndex;
    }

    public ReportStyle setStripedRowsIndex(StripedRowsIndex stripedRowsIndex) {
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

    public Color getBorderColor() {
        return borderColor;
    }

    public ReportStyle setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }
}
