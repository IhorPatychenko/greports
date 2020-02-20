package org.greports.styles;

import org.greports.styles.interfaces.StripedRows.StripedRowsIndex;
import org.greports.styles.stylesbuilders.AbstractReportStylesBuilder;
import org.greports.styles.stylesbuilders.HorizontalRangedStylesBuilder;
import org.greports.styles.stylesbuilders.PositionedStylesBuilder;
import org.greports.styles.stylesbuilders.RectangleRangedStylesBuilder;
import org.greports.styles.stylesbuilders.VerticalRangedStylesBuilder;

import java.awt.*;

public class ReportDataStyles {

    private VerticalRangedStylesBuilder rowStyles;
    private HorizontalRangedStylesBuilder columnStyles;
    private PositionedStylesBuilder positionedStyles;
    private RectangleRangedStylesBuilder rectangleRangedStylesBuilder;
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

    public RectangleRangedStylesBuilder getRectangleRangedStylesBuilder() {
        return rectangleRangedStylesBuilder;
    }

    public void setRectangleStyles(RectangleRangedStylesBuilder rectangleRangedStylesBuilder) {
        this.rectangleRangedStylesBuilder = rectangleRangedStylesBuilder;
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

        if(rectangleRangedStylesBuilder == null){
            rectangleRangedStylesBuilder = other.getRectangleRangedStylesBuilder();
        } else {
            rectangleRangedStylesBuilder.mergeStyles(other.getRectangleRangedStylesBuilder());
        }
    }

    public RectangleRangedStylesBuilder createRectangleRangedStylesBuilder(final AbstractReportStylesBuilder.StylePriority priority) {
        this.rectangleRangedStylesBuilder = new RectangleRangedStylesBuilder(priority);
        return this.rectangleRangedStylesBuilder;
    }
}
