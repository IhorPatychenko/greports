package org.greports.engine;

import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.styles.ReportStylesContainer;
import org.greports.styles.interfaces.StripedRows;
import org.greports.styles.interfaces.StyledReport;
import org.greports.utils.ReflectionUtils;

public abstract class ReportParser {

    protected  <T> void parseStyles(ReportData reportData, Class<T> clazz) throws ReportEngineReflectionException {
        try {
            if(StyledReport.class.isAssignableFrom(clazz) || StripedRows.class.isAssignableFrom(clazz)) {
                final T newInstance = ReflectionUtils.newInstance(clazz);
                final ReportStylesContainer reportStylesContainer = reportData.getStyles();
                if(StyledReport.class.isAssignableFrom(clazz)){
                    final StyledReport instance = (StyledReport) newInstance;
                    if(instance.getReportStyles(reportData.getRowsCount()) != null){
                        reportStylesContainer.setReportStylesBuilder(instance.getReportStyles(reportData.getRowsCount()).getOrDefault(reportData.getReportName(), null));
                    }
                }
                if(StripedRows.class.isAssignableFrom(clazz)){
                    final StripedRows instance = (StripedRows) newInstance;
                    if(instance.getStripedRowsIndex() != null && instance.getStripedRowsColor() != null){
                        reportStylesContainer
                                .setStripedRowsIndex(instance.getStripedRowsIndex().getOrDefault(reportData.getReportName(), null))
                                .setStripedRowsColor(instance.getStripedRowsColor().getOrDefault(reportData.getReportName(), null));
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new ReportEngineReflectionException("Error instantiating an object. The class should have an empty constructor without parameters", e, clazz);
        }
    }
}
