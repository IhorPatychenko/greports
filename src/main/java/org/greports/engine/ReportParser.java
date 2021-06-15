package org.greports.engine;

import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.styles.ReportStylesContainer;
import org.greports.styles.interfaces.StripedRows;
import org.greports.styles.interfaces.StyledReport;
import org.greports.utils.ReflectionUtils;

import java.lang.reflect.Method;

public abstract class ReportParser {

    private final String NESTED_VALUE_DELIMITER_REGEX = "\\.";

    protected <T> void parseStyles(final DataContainer<T> container) throws ReportEngineReflectionException {
        final Class<T> clazz = container.getClazz();
        final Data data = container.getReportData();
        if(StyledReport.class.isAssignableFrom(clazz) || StripedRows.class.isAssignableFrom(clazz)) {
            final T newInstance = ReflectionUtils.newInstance(clazz);
            final ReportStylesContainer reportStylesContainer = data.getStyles();
            if(StyledReport.class.isAssignableFrom(clazz)){
                final StyledReport instance = (StyledReport) newInstance;
                if(instance.getReportStyles(data.getRowsCount()) != null){
                    reportStylesContainer.setReportStylesBuilder(instance.getReportStyles(data.getRowsCount()).getOrDefault(data.getReportName(), null));
                }
            }
            if(StripedRows.class.isAssignableFrom(clazz)){
                final StripedRows instance = (StripedRows) newInstance;
                if(instance.getStripedRowsIndex() != null && instance.getStripedRowsColor() != null){
                    reportStylesContainer
                            .setStripedRowsIndex(instance.getStripedRowsIndex().getOrDefault(data.getReportName(), null))
                            .setStripedRowsColor(instance.getStripedRowsColor().getOrDefault(data.getReportName(), null));
                }
            }
        }
    }

    protected <T> Object checkNestedValue(T dto, Method method, boolean nested, String target) throws ReportEngineReflectionException {
        Object invokedValue = dto != null ? ReflectionUtils.invokeMethod(method, dto) : null;
        if(nested) {
            final String[] split = target.split(NESTED_VALUE_DELIMITER_REGEX);
            short nestedCount = 0;
            while(nestedCount < split.length) {
                if(invokedValue == null) {
                    return null;
                }
                method = ReflectionUtils.fetchFieldGetter(split[nestedCount], invokedValue.getClass());
                invokedValue = ReflectionUtils.invokeMethod(method, invokedValue);
                nestedCount++;
            }
        }
        return invokedValue;
    }
}
