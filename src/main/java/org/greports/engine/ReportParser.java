package org.greports.engine;

import org.greports.content.ReportData;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.styles.ReportDataStyles;
import org.greports.styles.interfaces.StripedRows;
import org.greports.styles.interfaces.StyledReport;
import org.greports.utils.ReflectionUtils;

public abstract class ReportParser {

    protected  <T> void parseStyles(ReportData reportData, Class<T> clazz) throws ReportEngineReflectionException {
        try {
            final T newInstance = ReflectionUtils.newInstance(clazz);
            final ReportDataStyles reportDataStyles = reportData.getStyles();
            if(newInstance instanceof StyledReport){
                final StyledReport instance = (StyledReport) newInstance;
                if(instance.getRangedRowStyles() != null){
                    reportDataStyles.setRowStyles(instance.getRangedRowStyles().getOrDefault(reportData.getReportName(), null));
                }
                if(instance.getRangedColumnStyles() != null){
                    reportDataStyles.setColumnStyles(instance.getRangedColumnStyles().getOrDefault(reportData.getReportName(), null));
                }
                if(instance.getPositionedStyles() != null){
                    reportDataStyles.setPositionedStyles(instance.getPositionedStyles().getOrDefault(reportData.getReportName(), null));
                }
                if(instance.getRectangleRangedStyles() != null){
                    reportDataStyles.setRectangleStyles(instance.getRectangleRangedStyles().getOrDefault(reportData.getReportName(), null));
                }
            }
            if(newInstance instanceof StripedRows){
                final StripedRows instance = (StripedRows) newInstance;
                if(instance.getStripedRowsIndex() != null && instance.getStripedRowsColor() != null){
                    reportDataStyles
                            .setStripedRowsIndex(instance.getStripedRowsIndex().getOrDefault(reportData.getReportName(), null))
                            .setStripedRowsColor(instance.getStripedRowsColor().getOrDefault(reportData.getReportName(), null));
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new ReportEngineReflectionException("Error instantiating an object. The class should have an empty constructor without parameters", e, clazz);
        }
    }
}
