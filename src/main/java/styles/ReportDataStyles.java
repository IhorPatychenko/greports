package styles;

import styles.interfaces.StripedRows.StripedRowsIndex;
import styles.stylesbuilders.HorizontalRangedStylesBuilder;
import styles.stylesbuilders.PositionedStylesBuilder;
import styles.stylesbuilders.RectangleRangedStylesBuilder;
import styles.stylesbuilders.VerticalRangedStylesBuilder;

import java.awt.*;

public class ReportDataStyles {

    private VerticalRangedStylesBuilder rowStyles;
    private HorizontalRangedStylesBuilder columnStyles;
    private PositionedStylesBuilder positionedStyles;
    private RectangleRangedStylesBuilder rangedStyleReportStyles;
    private StripedRowsIndex stripedRowsIndex;
    private Color stripedRowsColor;

    public VerticalRangedStylesBuilder getRowStyles() {
        return rowStyles;
    }

    public void setRowStyles(VerticalRangedStylesBuilder rowStyles) {
        this.rowStyles = rowStyles;
    }

    public HorizontalRangedStylesBuilder getColumnStyles() {
        return columnStyles;
    }

    public void setColumnStyles(HorizontalRangedStylesBuilder columnStyles) {
        this.columnStyles = columnStyles;
    }

    public PositionedStylesBuilder getPositionedStyles() {
        return positionedStyles;
    }

    public void setPositionedStyles(PositionedStylesBuilder positionedStyles) {
        this.positionedStyles = positionedStyles;
    }

    public RectangleRangedStylesBuilder getRangedStyleReportStyles() {
        return rangedStyleReportStyles;
    }

    public void setRangedStyleReportStyles(RectangleRangedStylesBuilder rangedStyleReportStyles) {
        this.rangedStyleReportStyles = rangedStyleReportStyles;
    }

    public StripedRowsIndex getStripedRowsIndex() {
        return stripedRowsIndex;
    }

    public ReportDataStyles setStripedRowsIndex(StripedRowsIndex stripedRowsIndex) {
        this.stripedRowsIndex = stripedRowsIndex;
        return this;
    }

    public Color getStripedRowsColor() {
        return stripedRowsColor;
    }

    public ReportDataStyles setStripedRowsColor(Color stripedRowsColor) {
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
