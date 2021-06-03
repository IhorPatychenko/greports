package org.greports.styles;

import lombok.Getter;
import org.greports.styles.interfaces.StripedRows.StripedRowsIndex;
import org.greports.styles.stylesbuilders.ReportStylesBuilder;

import java.awt.*;
import java.io.Serializable;

@Getter
public class ReportStylesContainer implements Serializable {
    private static final long serialVersionUID = 3171712599995917074L;

    private ReportStylesBuilder reportStylesBuilder;
    private StripedRowsIndex stripedRowsIndex;
    private Color stripedRowsColor;

    public void setReportStylesBuilder(ReportStylesBuilder reportStylesBuilder) {
        this.reportStylesBuilder = reportStylesBuilder;
    }

    public ReportStylesContainer setStripedRowsIndex(StripedRowsIndex stripedRowsIndex) {
        this.stripedRowsIndex = stripedRowsIndex;
        return this;
    }

    public Color getStripedRowsColor() {
        return stripedRowsColor;
    }

    public ReportStylesContainer setStripedRowsColor(Color stripedRowsColor) {
        this.stripedRowsColor = stripedRowsColor;
        return this;
    }

    public void mergeStyles(ReportStylesContainer other) {
        if(reportStylesBuilder == null){
            reportStylesBuilder = other.getReportStylesBuilder();
        } else {
            reportStylesBuilder.mergeStyles(other.getReportStylesBuilder());
        }
    }

    public ReportStylesBuilder createReportStylesBuilder() {
        this.reportStylesBuilder = new ReportStylesBuilder();
        return this.reportStylesBuilder;
    }
}
