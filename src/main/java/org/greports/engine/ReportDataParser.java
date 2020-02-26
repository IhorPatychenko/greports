package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.apache.log4j.Level;
import org.greports.annotations.Column;
import org.greports.annotations.Configuration;
import org.greports.annotations.SpecialColumn;
import org.greports.annotations.SpecialRowCell;
import org.greports.annotations.SpecialRow;
import org.greports.annotations.Subreport;
import org.greports.content.column.ReportDataCell;
import org.greports.content.cell.ReportDataSpecialRowCell;
import org.greports.content.cell.ReportHeaderCell;
import org.greports.content.ReportData;
import org.greports.content.row.ReportDataRow;
import org.greports.content.ReportHeader;
import org.greports.content.row.ReportDataSpecialRow;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.exceptions.ReportEngineRuntimeException;
import org.greports.interfaces.CollectedValues;
import org.greports.services.LoggerService;
import org.greports.styles.ReportDataStyles;
import org.greports.styles.interfaces.ConditionalRowStyles;
import org.greports.styles.interfaces.StripedRows;
import org.greports.styles.interfaces.StyledReport;
import org.greports.positioning.TranslationsParser;
import org.greports.styles.stylesbuilders.AbstractReportStylesBuilder;
import org.greports.styles.stylesbuilders.HorizontalRangedStyleBuilder;
import org.greports.styles.stylesbuilders.RectangleRangedStyleBuilder;
import org.greports.styles.stylesbuilders.RectangleRangedStylesBuilder;
import org.greports.utils.AnnotationUtils;
import org.greports.utils.ConverterUtils;
import org.greports.utils.Pair;
import org.greports.utils.ReflectionUtils;
import org.greports.utils.Translator;
import org.greports.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

final class ReportDataParser {

    private LoggerService loggerService;

    private ReportData reportData;
    private Translator translator;
    private Configuration configuration;
    private List<ReportData> subreportsData = new ArrayList<>();
    private static final float SUBREPORT_POSITIONAL_INCREMENT = 0.00000000000001f;

    public ReportDataParser(boolean loggerEnabled, Level level) {
        loggerService = LoggerService.forClass(ReportDataParser.class, loggerEnabled, level);
    }

    protected <T> ReportDataParser parse(Collection<T> collection, final String reportName, Class<T> clazz, ReportConfigurator configurator) throws ReportEngineReflectionException, ReportEngineRuntimeException {
        loggerService.info("Parsing started...");
        loggerService.info("Parsing report with name \"" + reportName + "\"...");
        Stopwatch timer = Stopwatch.createStarted();
        final ReportDataParser parser = parse(collection, reportName, clazz, 0f);
        overrideSheetName(configurator.getSheetName());
        overrideSubreportsTitles(configurator.getOverriddenTitles());
        loggerService.info("Report with name \"" + reportName + "\" successfully parsed. Parse time: " + timer.stop());
        return parser;
    }

    private <T> ReportDataParser parse(Collection<T> collection, final String reportName, Class<T> clazz, Float positionIncrement) throws ReportEngineReflectionException, ReportEngineRuntimeException {
        configuration = AnnotationUtils.getReportConfiguration(clazz, reportName);
        reportData = new ReportData(reportName, configuration.sheetName(), !configuration.templatePath().equals("") ? getClass().getClassLoader().getResource(configuration.templatePath()) : null);
        final Map<String, Object> translations = new TranslationsParser(configuration.translationsDir()).parse(Utils.getLocale(configuration.locale()).getLanguage());
        translator = new Translator(translations);
        subreportsData = new ArrayList<>();
        parseReportHeader(clazz, positionIncrement);
        parseRowsData(collection, clazz, positionIncrement);
        parseSpecialColumns(collection, clazz);
        parseSpecialRows(collection, clazz);
        parseStyles(collection, clazz);
        parseSubreports(collection, clazz);
        reportData.mergeReportData(subreportsData);
        return this;
    }

    private <T> void parseReportHeader(Class<T> clazz, Float positionIncrement) throws ReportEngineReflectionException {
        reportData.setCreateHeader(configuration.createHeader());
        reportData.setHeaderStartRow(configuration.headerRowIndex());
        List<ReportHeaderCell> cells = new ArrayList<>();
        final Function<Pair<Method, Column>, Void> columnFunction = AnnotationUtils.getHeadersFunction(cells, translator, positionIncrement);
        AnnotationUtils.methodsWithColumnAnnotations(clazz, columnFunction, reportData.getReportName());

        for (SpecialColumn specialColumn : configuration.specialColumns()) {
            cells.add(new ReportHeaderCell(specialColumn.position(), specialColumn.title(), specialColumn.id(), specialColumn.autoSizeColumn()));
        }

        reportData
                .setHeader(new ReportHeader(configuration.sortableHeader()))
                .addCells(cells);
    }

    private <T> void parseRowsData(Collection<T> collection, Class<T> clazz, Float positionIncrement) throws ReportEngineReflectionException {
        reportData.setDataStartRow(configuration.dataStartRowIndex());

        Map<Method, Column> columnsMap = new LinkedHashMap<>();
        Function<Pair<Method, Column>, Void> columnFunction = AnnotationUtils.getMethodsAndColumnsFunction(columnsMap);
        AnnotationUtils.methodsWithColumnAnnotations(clazz, columnFunction, reportData.getReportName());

        try {
            for (T dto : collection) {
                ReportDataRow row = new ReportDataRow();
                for (final Map.Entry<Method, Column> entry : columnsMap.entrySet()) {
                    final Column column = entry.getValue();
                    final Method method = entry.getKey();
                    method.setAccessible(true);
                    Object invokedValue = dto != null ? method.invoke(dto) : null;

                    if(column.getterConverter().length > 1){
                        throw new ReportEngineRuntimeException("A column cannot have more than 1 getter converter", clazz);
                    } else if(column.getterConverter().length == 1){
                        invokedValue = ConverterUtils.convertValue(invokedValue, column.getterConverter()[0]);
                    }

                    ReportDataCell reportDataCell = new ReportDataCell(
                        column.position() + positionIncrement,
                        column.format(),
                        invokedValue,
                        column.valueType()
                    );
                    row.addCell(reportDataCell);
                }
                reportData.addRow(row);
            }
        } catch (IllegalAccessException e) {
            throw new ReportEngineReflectionException("Error invoking the method with no access", e, clazz);
        } catch (InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error invoking the method", e, clazz);
        }
    }

    private <T> void parseSubreports(Collection<T> collection, Class<T> clazz) throws ReportEngineReflectionException {
        final ReportDataParser reportDataParser = new ReportDataParser(this.loggerService.isEnabled(), this.loggerService.getLevel());
        Map<Method, Subreport> subreportMap = new LinkedHashMap<>();
        Function<Pair<Method, Subreport>, Void> subreportFunction = AnnotationUtils.getSubreportsFunction(subreportMap);
        AnnotationUtils.methodsWithSubreportAnnotations(clazz, subreportFunction, reportData.getReportName());

        for (Map.Entry<Method, Subreport> entry : subreportMap.entrySet()) {
            final Method method = entry.getKey();
            final Subreport subreport = entry.getValue();
            Class<?> returnType = method.getReturnType();
            method.setAccessible(true);
            Class<?> componentType = returnType;

            if(ReflectionUtils.isListOrArray(returnType)){

                List<List<?>> subreportsList = new ArrayList<>();
                if(returnType.isArray()){
                    componentType = returnType.getComponentType();
                } else {
                    ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
                    componentType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                }
                float subreportPositionalIncrement = Math.max(AnnotationUtils.getSubreportLastColumn(componentType, reportData.getReportName()).position(), SUBREPORT_POSITIONAL_INCREMENT) + SUBREPORT_POSITIONAL_INCREMENT;

                for (T collectionEntry : collection) {
                    final Object invokeResult = subreportInvokeMethod(method, collectionEntry);
                    if(returnType.isArray()){
                        subreportsList.add(new ArrayList<>(Arrays.asList((Object[]) invokeResult)));
                    } else {
                        subreportsList.add((List) invokeResult);
                    }
                }

                if(subreportsList.size() > 0){
                    float positionalIncrement = subreportPositionalIncrement;
                    final int subreportsInEveryList = subreportsList.get(0).size();
                    for (int i = 0; i < subreportsInEveryList; i++) {
                        final Collection<Object> subreportData = new ArrayList<>();
                        for (final List<?> list : subreportsList) {
                            subreportData.add(list.get(i));
                        }
                        parseSubreportData(reportDataParser, componentType, subreportData, positionalIncrement + subreport.position());
                        positionalIncrement += subreportPositionalIncrement;
                    }
                }
            } else {
                float subreportPositionalIncrement = AnnotationUtils.getSubreportLastColumn(componentType, reportData.getReportName()).position() + SUBREPORT_POSITIONAL_INCREMENT;
                final Collection<Object> subreportData = new ArrayList<>();
                for (T collectionEntry : collection) {
                    final Object invokeResult = subreportInvokeMethod(method, collectionEntry);
                    subreportData.add(invokeResult);
                }
                parseSubreportData(reportDataParser, returnType, subreportData, subreportPositionalIncrement + subreport.position());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void parseSubreportData(final ReportDataParser reportDataParser, final Class<?> returnType, final Collection subreportData, float positionalIncrement) throws ReportEngineReflectionException {
        final ReportData data = reportDataParser.parse(subreportData, reportData.getReportName(), returnType, positionalIncrement).getData();
        subreportsData.add(data);
    }

    private <T> Object subreportInvokeMethod(Method method, T collectionEntry) throws ReportEngineReflectionException {
        try {
            return method.invoke(collectionEntry);
        } catch (IllegalAccessException e) {
            throw new ReportEngineReflectionException("Error invoking the method with no access", e, method.getDeclaringClass());
        } catch (InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error invoking the method", e, method.getDeclaringClass());
        }
    }

    private <T> void parseSpecialColumns(Collection<T> collection, Class<T> clazz) throws ReportEngineReflectionException {
        List<T> list = new ArrayList<>(collection);
        for (SpecialColumn specialColumn : configuration.specialColumns()) {
            Method method = null;
            if(ValueType.METHOD.equals(specialColumn.valueType())){
                method = ReflectionUtils.getMethodWithName(clazz, specialColumn.value(), new Class<?>[]{});
            }
            for (int i = 0; i < list.size(); i++) {
                Object value = specialColumn.value();
                try {
                    if(method != null) {
                        final T listElement = list.get(i);
                        method.setAccessible(true);
                        value = method.invoke(listElement);
                    }
                    reportData.getRows().get(i).addCell(new ReportDataCell(
                            specialColumn.position(),
                            specialColumn.format(),
                            value,
                            specialColumn.valueType()
                    ));
                } catch (IllegalAccessException e) {
                    throw new ReportEngineReflectionException("Error invoking the method with no access", e, clazz);
                } catch (InvocationTargetException e) {
                    throw new ReportEngineReflectionException("Error invoking the method", e, clazz);
                }
            }
        }
    }

    private <T> void parseSpecialRows(Collection<T> collection, Class<T> clazz) throws ReportEngineReflectionException {
        for(SpecialRow specialRow : configuration.specialRows()){
            final ReportDataSpecialRow reportDataSpecialRow = new ReportDataSpecialRow(specialRow.rowIndex());
            for (final SpecialRowCell specialRowCell : specialRow.cells()) {
                if(!specialRowCell.valueType().equals(ValueType.COLLECTED_VALUE)) {
                    reportDataSpecialRow.addCell(new ReportDataSpecialRowCell(specialRowCell.valueType(), specialRowCell.value(), specialRowCell.format(), specialRowCell.targetId()));
                } else if(CollectedValues.class.isAssignableFrom(clazz)){
                    try {
                        final T newInstance = ReflectionUtils.newInstance(clazz);
                        final CollectedValues instance = (CollectedValues) newInstance;
                        final List<Object> list = new ArrayList<>();
                        for (final T t : collection) {
                            final CollectedValues values = (CollectedValues) t;
                            list.add(values.getCollectedValue().get(Pair.of(reportData.getReportName(), specialRowCell.value())));
                        }
                        final Object value = instance.getCollectedValuesResult(list);
                        reportDataSpecialRow.addCell(new ReportDataSpecialRowCell(specialRowCell.valueType(), value, specialRowCell.format(), specialRowCell.targetId()));
                    } catch (ReflectiveOperationException e) {
                        throw new ReportEngineReflectionException("Error instantiating an object. The class should have an empty constructor without parameters", e, clazz);
                    }
                }
            }
            reportData.addSpecialRow(reportDataSpecialRow);
        }
    }

    private <T> void parseStyles(Collection<T> collection, Class<T> clazz) throws ReportEngineReflectionException {
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
            if(newInstance instanceof ConditionalRowStyles){
                final List<T> list = new ArrayList<>(collection);
                final short startRowIndex = configuration.dataStartRowIndex();
                RectangleRangedStylesBuilder rectangleRangedStylesBuilder = reportData.getStyles().getRectangleRangedStylesBuilder();
                if(rectangleRangedStylesBuilder == null){
                    rectangleRangedStylesBuilder = reportData.getStyles().createRectangleRangedStylesBuilder(AbstractReportStylesBuilder.StylePriority.PRIORITY4);
                }
                for (int i = 0; i < list.size(); i++) {
                    final T entry = list.get(i);
                    final ConditionalRowStyles conditionalRowStyles = (ConditionalRowStyles) entry;
                    final Predicate<Integer> predicate = conditionalRowStyles.isStyled().get(reportData.getReportName());
                    if(predicate != null && predicate.test(i)){
                        final List<HorizontalRangedStyleBuilder> horizontalRangedStyleBuilders = conditionalRowStyles.getIndexBasedStyle().getOrDefault(reportData.getReportName(), new ArrayList<>());
                        for (final HorizontalRangedStyleBuilder styleBuilder : horizontalRangedStyleBuilders) {
                            final RectangleRangedStyleBuilder rectangleRangedStyleBuilder = new RectangleRangedStyleBuilder(styleBuilder, startRowIndex + i);
                            rectangleRangedStylesBuilder.addStyleBuilder(rectangleRangedStyleBuilder);
                        }
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new ReportEngineReflectionException("Error instantiating an object. The class should have an empty constructor without parameters", e, clazz);
        }
    }

    private void overrideSheetName(final String sheetName) {
        if(sheetName != null){
            reportData.setSheetName(sheetName);
        }
    }

    private void overrideSubreportsTitles(final Map<Integer, String> overriddenTitles) {
        for (final Map.Entry<Integer, String> entry : overriddenTitles.entrySet()) {
            this.reportData.getHeader().getCell(entry.getKey()).setTitle(entry.getValue());
        }
    }

    ReportData getData(){
        return reportData;
    }

}