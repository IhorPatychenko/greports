package org.greports.styles;

import lombok.Getter;
import org.greports.styles.stylesbuilders.ReportStylesBuilder;

import java.awt.*;
import java.io.Serializable;

@Getter
public class StylesContainer implements Serializable {
    private static final long serialVersionUID = 3171712599995917074L;

    private ReportStylesBuilder reportStylesBuilder;
    private Color stripedRowsColor;

    public void setReportStylesBuilder(ReportStylesBuilder reportStylesBuilder) {
        this.reportStylesBuilder = reportStylesBuilder;
    }

    public Color getStripedRowsColor() {
        return stripedRowsColor;
    }

    public StylesContainer setStripedRowsColor(Color stripedRowsColor) {
        this.stripedRowsColor = stripedRowsColor;
        return this;
    }

    public void mergeStyles(StylesContainer other) {
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
