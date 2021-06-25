package org.greports.engine;

import org.greports.exceptions.GreportsReflectionException;
import org.greports.styles.NotImplementedStyles;
import org.greports.styles.StylesContainer;
import org.greports.styles.interfaces.StyledReport;
import org.greports.styles.stylesbuilders.ReportStylesBuilder;
import org.greports.utils.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;

public abstract class Parser {

    private final String NESTED_VALUE_DELIMITER_REGEX = "\\.";

    protected <T> void parseStyles(final DataContainer<T> container) throws GreportsReflectionException {
        final Data data = container.getReportData();
        Class<? extends StyledReport> stylesClass = data.getConfiguration().getStyles();
        if(isStyledReport(stylesClass)) {
            final StylesContainer stylesContainer = data.getStyles();
            if(isStyledReport(stylesClass)){
                final StyledReport stylesInstance = ReflectionUtils.newInstance(stylesClass);
                Map<String, ReportStylesBuilder> stylesMap = stylesInstance.getReportStyles(data.getRowsCount());
                if(stylesMap != null){
                    stylesContainer.setReportStylesBuilder(stylesMap.getOrDefault(data.getReportName(), null));
                }
            }
        }
    }

    protected <T> Object checkNestedValue(T dto, Method method, boolean nested, String target) throws GreportsReflectionException {
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

    private boolean isStyledReport(Class<? extends StyledReport> styleClass) {
        return !NotImplementedStyles.class.equals(styleClass) && StyledReport.class.isAssignableFrom(styleClass);
    }
}
