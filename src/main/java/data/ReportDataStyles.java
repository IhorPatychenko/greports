package data;

import org.apache.poi.ss.usermodel.IndexedColors;
import styles.HorizontalRangedStyle;
import styles.PositionedStyle;
import styles.RectangleRangedStyle;
import styles.ReportStylesBuilder;
import styles.VerticalRangedStyle;
import styles.interfaces.StripedRows.StripedRowsIndex;

public class ReportDataStyles {

    private ReportStylesBuilder<VerticalRangedStyle> rowStyles;
    private ReportStylesBuilder<HorizontalRangedStyle> columnStyles;
    private ReportStylesBuilder<PositionedStyle> positionedStyles;
    private ReportStylesBuilder<RectangleRangedStyle> rangedStyleReportStyles;
    private StripedRowsIndex stripedRowsIndex;
    private IndexedColors stripedRowsColor;

    public ReportStylesBuilder<VerticalRangedStyle> getRowStyles() {
        return rowStyles;
    }

    public void setRowStyles(ReportStylesBuilder<VerticalRangedStyle> rowStyles) {
        this.rowStyles = rowStyles;
    }

    public ReportStylesBuilder<HorizontalRangedStyle> getColumnStyles() {
        return columnStyles;
    }

    public void setColumnStyles(ReportStylesBuilder<HorizontalRangedStyle> columnStyles) {
        this.columnStyles = columnStyles;
    }

    public ReportStylesBuilder<PositionedStyle> getPositionedStyles() {
        return positionedStyles;
    }

    public void setPositionedStyles(ReportStylesBuilder<PositionedStyle> positionedStyles) {
        this.positionedStyles = positionedStyles;
    }

    public ReportStylesBuilder<RectangleRangedStyle> getRangedStyleReportStyles() {
        return rangedStyleReportStyles;
    }

    public void setRangedStyleReportStyles(ReportStylesBuilder<RectangleRangedStyle> rangedStyleReportStyles) {
        this.rangedStyleReportStyles = rangedStyleReportStyles;
    }

    public StripedRowsIndex getStripedRowsIndex() {
        return stripedRowsIndex;
    }

    public ReportDataStyles setStripedRowsIndex(StripedRowsIndex stripedRowsIndex) {
        this.stripedRowsIndex = stripedRowsIndex;
        return this;
    }

    public IndexedColors getStripedRowsColor() {
        return stripedRowsColor;
    }

    public ReportDataStyles setStripedRowsColor(IndexedColors stripedRowsColor) {
        this.stripedRowsColor = stripedRowsColor;
        return this;
    }
}
