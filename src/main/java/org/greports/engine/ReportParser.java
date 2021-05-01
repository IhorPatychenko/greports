package org.greports.engine;

import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.exceptions.ReportEngineRuntimeException;
import org.greports.styles.ReportStylesContainer;
import org.greports.styles.interfaces.StripedRows;
import org.greports.styles.interfaces.StyledReport;
import org.greports.utils.ReflectionUtils;

import java.lang.reflect.Method;

public abstract class ReportParser {

    private final String NESTED_VALUE_DELIMITER_REGEX = "\\.";

    protected <T> void parseStyles(final ReportGenericDataContainer<T> container) throws ReportEngineReflectionException {
        final Class<T> clazz = container.getClazz();
        final ReportData reportData = container.getReportData();
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
    }

    protected <T> Object checkNestedValue(T dto, Method method, boolean isNested, String target) throws ReportEngineReflectionException {
        Object invokedValue = dto != null ? ReflectionUtils.invokeMethod(method, dto) : null;
        if(isNested) {
            final String[] split = target.split(NESTED_VALUE_DELIMITER_REGEX);
            short nestedCount = 0;
            while(nestedCount < split.length) {
                if(invokedValue == null) {
                    throw new ReportEngineRuntimeException(String.format("Nested field %s cannot be null", split[nestedCount]), ReportDataParser.class);
                }
                method = ReflectionUtils.fetchFieldGetter(split[nestedCount], invokedValue.getClass());
                invokedValue = ReflectionUtils.invokeMethod(method, invokedValue);
                nestedCount++;
            }
        }
        return invokedValue;
    }
}
