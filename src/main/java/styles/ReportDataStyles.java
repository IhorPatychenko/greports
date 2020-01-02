package styles;

import org.apache.poi.ss.usermodel.IndexedColors;
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

    public void mergeStyles(ReportDataStyles other) {
        if(rowStyles == null){
            rowStyles = other.getRowStyles();
        } else {
            rowStyles.mergeStyles(other.getRowStyles());
        }

        if(columnStyles == null){
            columnStyles = other.getColumnStyles();
        } else {
            columnStyles.mergeStyles(other.getColumnStyles());
        }

        if(positionedStyles == null){
            positionedStyles = other.getPositionedStyles();
        } else {
            positionedStyles.mergeStyles(other.getPositionedStyles());
        }

        if(rangedStyleReportStyles == null){
            rangedStyleReportStyles = other.getRangedStyleReportStyles();
        } else {
            rangedStyleReportStyles.mergeStyles(other.getRangedStyleReportStyles());
        }
    }
}