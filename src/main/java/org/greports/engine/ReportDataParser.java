package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.apache.log4j.Level;
import org.greports.annotations.Column;
import org.greports.annotations.Configuration;
import org.greports.annotations.Subreport;
import org.greports.content.ReportHeader;
import org.greports.content.cell.DataCell;
import org.greports.content.cell.HeaderCell;
import org.greports.content.cell.SpecialDataCell;
import org.greports.content.row.DataRow;
import org.greports.content.row.SpecialDataRow;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.exceptions.ReportEngineRuntimeException;
import org.greports.interfaces.CollectedFormulaValues;
import org.greports.interfaces.CollectedValues;
import org.greports.interfaces.GroupedRows;
import org.greports.positioning.TranslationsParser;
import org.greports.services.LoggerService;
import org.greports.styles.interfaces.ConditionalRowStyles;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

final class ReportDataParser extends ReportParser {

    private LoggerService loggerService;

    private ReportData reportData;
    private Translator translator;
    private ReportConfigurator reportConfigurator;
    private List<ReportData> subreportsData = new ArrayList<>();
    private static final float SUBREPORT_POSITIONAL_INCREMENT = 0.00000000000001f;

    public ReportDataParser(boolean loggerEnabled, Level level) {
        loggerService = LoggerService.forClass(ReportDataParser.class, loggerEnabled, level);
    }

    protected <T> ReportDataParser parse(List<T> collection, final String reportName, Class<T> clazz, ReportConfigurator configurator) throws ReportEngineReflectionException, ReportEngineRuntimeException {
        loggerService.info("Parsing started...");
        loggerService.info(String.format("Parsing report for class \"%s\" with name \"%s\"...", clazz.getSimpleName(), reportName));
        Stopwatch timer = Stopwatch.createStarted();
        final ReportDataParser parser = parse(collection, reportName, clazz, 0f, configurator);
        overrideSheetName(configurator.getSheetName());
        overrideSubreportsTitles(configurator.getOverriddenTitles());
        loggerService.info(String.format("Report with name \"%s\" successfully parsed. Parse time: %s", reportName, timer.stop()));
        return parser;
    }

    private <T> ReportDataParser parse(List<T> collection, final String reportName, Class<T> clazz, Float positionIncrement, ReportConfigurator configurator) throws ReportEngineReflectionException, ReportEngineRuntimeException {
        final Configuration configuration = AnnotationUtils.getReportConfiguration(clazz, reportName);
        reportData = new ReportData(reportName, configuration, !configuration.templatePath().equals("") ? getClass().getClassLoader().getResource(configuration.templatePath()) : null);
        final Map<String, Object> translations = new TranslationsParser(reportData.getConfiguration().getTranslationsDir()).parse(Utils.getLocale(reportData.getConfiguration().getLocale()));
        translator = new Translator(translations);
        subreportsData = new ArrayList<>();
        reportConfigurator = configurator;
        parseReportHeader(clazz, positionIncrement);
        parseRowsData(collection, clazz, positionIncrement);
        parseGroupRows(collection, clazz);
        parseSpecialColumns(collection, clazz);
        parseSpecialRows(collection, clazz);
        parseStyles(collection, clazz);
        parseSubreports(collection, clazz);
        reportData.mergeReportData(subreportsData);
        return this;
    }

    private <T> void parseReportHeader(Class<T> clazz, Float positionIncrement) throws ReportEngineReflectionException {
        reportData.setCreateHeader(reportData.getConfiguration().isCreateHeader());
        List<HeaderCell> cells = new ArrayList<>();
        final Function<Pair<Method, Column>, Void> columnFunction = AnnotationUtils.getHeadersFunction(cells, translator, positionIncrement);
        AnnotationUtils.methodsWithColumnAnnotations(clazz, columnFunction, reportData.getReportName());

        for (ReportSpecialColumn specialColumn : reportData.getConfiguration().getSpecialColumns()) {
            cells.add(new HeaderCell(
                specialColumn.getPosition(),
                specialColumn.getTitle(),
                specialColumn.getId(),
                specialColumn.isAutoSizeColumn(),
                specialColumn.getColumnWidth()
            ));
        }

        reportData
                .setHeader(new ReportHeader(
                        reportData.getConfiguration().isSortableHeader(),
                        reportData.getConfiguration().getHeaderRowIndex()))
                .addCells(cells);
    }

    private <T> void parseRowsData(List<T> collection, Class<T> clazz, Float positionIncrement) throws ReportEngineReflectionException {
        reportData.setDataStartRow(reportData.getConfiguration().getDataStartRowIndex());

        Map<Method, Column> columnsMap = new LinkedHashMap<>();
        Function<Pair<Method, Column>, Void> columnFunction = AnnotationUtils.getMethodsAndColumnsFunction(columnsMap);
        AnnotationUtils.methodsWithColumnAnnotations(clazz, columnFunction, reportData.getReportName());

        List<T> dataList = new ArrayList<>(collection);

        try {
            for (int i = 0; i < dataList.size(); i++) {
                T dto = dataList.get(i);
                DataRow row = new DataRow(reportData.getConfiguration().getDataStartRowIndex() + i);
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

                    String format = column.format();

                    if(invokedValue != null) {
                        final String formatForClass = reportConfigurator.getFormatForClass(invokedValue.getClass());
                        if(format.isEmpty()){
                            format = formatForClass;
                        }
                    }

                    final DataCell dataCell = new DataCell(
                        column.position() + positionIncrement,
                        false,
                        format,
                        invokedValue,
                        column.valueType(),
                        column.columnWidth()
                    );
                    row.addCell(dataCell);
                }
                reportData.addRow(row);
            }
        } catch (IllegalAccessException e) {
            throw new ReportEngineReflectionException("Error invoking the method with no access", e, clazz);
        } catch (InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error invoking the method", e, clazz);
        }
    }

    private <T> void parseGroupRows(final List<T> list, final Class<T> clazz) throws ReportEngineReflectionException {
        if(GroupedRows.class.isAssignableFrom(clazz)){
            try {
                final GroupedRows newInstance = (GroupedRows) ReflectionUtils.newInstance(clazz);
                reportData.setGroupedRowsDefaultCollapsed(newInstance.isDefaultCollapsed().get(reportData.getReportName()).getAsBoolean());
                Pair<Integer, Integer> group;
                Integer groupStart = null;
                for(int i = 0; i < list.size(); i++) {
                    GroupedRows groupedRows = (GroupedRows) list.get(i);
                    if(groupedRows.isGroupStartRow().get(reportData.getReportName()).test(i)){
                        groupStart = i;
                    }
                    if(groupedRows.isGroupEndRow().get(reportData.getReportName()).test(i)) {
                        group = Pair.of(groupStart, i);
                        reportData.addGroupedRow(group);
                    }
                }
            } catch (ReflectiveOperationException e) {
                throw new ReportEngineReflectionException("Error instantiating an object. The class should have an empty constructor without parameters", e, clazz);
            }
        }
    }

    private <T> void parseSubreports(List<T> collection, Class<T> clazz) throws ReportEngineReflectionException {
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
                        subreportsList.add((List<?>) invokeResult);
                    }
                }

                if(subreportsList.size() > 0){
                    float positionalIncrement = subreportPositionalIncrement;
                    final int subreportsInEveryList = subreportsList.get(0).size();
                    for (int i = 0; i < subreportsInEveryList; i++) {
                        final List<Object> subreportData = new ArrayList<>();
                        for (final List<?> list : subreportsList) {
                            subreportData.add(list.get(i));
                        }
                        parseSubreportData(reportDataParser, componentType, subreportData, positionalIncrement + subreport.position());
                        positionalIncrement += subreportPositionalIncrement;
                    }
                }
            } else {
                float subreportPositionalIncrement = AnnotationUtils.getSubreportLastColumn(componentType, reportData.getReportName()).position() + SUBREPORT_POSITIONAL_INCREMENT;
                final List<Object> subreportData = new ArrayList<>();
                for (T collectionEntry : collection) {
                    final Object invokeResult = subreportInvokeMethod(method, collectionEntry);
                    subreportData.add(invokeResult);
                }
                parseSubreportData(reportDataParser, returnType, subreportData, subreportPositionalIncrement + subreport.position());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void parseSubreportData(final ReportDataParser reportDataParser, final Class<?> returnType, final List subreportData, float positionalIncrement) throws ReportEngineReflectionException {
        final ReportData data = reportDataParser.parse(subreportData, reportData.getReportName(), returnType, positionalIncrement, reportConfigurator.getReportGenerator().getConfigurator(returnType, reportData.getReportName())).getData();
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

    private <T> void parseSpecialColumns(List<T> collection, Class<T> clazz) throws ReportEngineReflectionException {
        List<T> list = new ArrayList<>(collection);
        for (ReportSpecialColumn specialColumn : reportData.getConfiguration().getSpecialColumns()) {
            Method method = null;
            if(ValueType.METHOD.equals(specialColumn.getValueType())){
                method = ReflectionUtils.getMethodWithName(clazz, specialColumn.getValue(), new Class<?>[]{});
            }
            for (int i = 0; i < list.size(); i++) {
                Object value = specialColumn.getValue();
                try {
                    if(method != null) {
                        final T listElement = list.get(i);
                        method.setAccessible(true);
                        value = method.invoke(listElement);
                    }
                    reportData.getDataRows().get(i).addCell(new DataCell(
                        specialColumn.getPosition(),
                        false,
                        specialColumn.getFormat(),
                        value,
                        specialColumn.getValueType(),
                        specialColumn.getColumnWidth()
                    ));
                } catch (IllegalAccessException e) {
                    throw new ReportEngineReflectionException("Error invoking the method with no access", e, clazz);
                } catch (InvocationTargetException e) {
                    throw new ReportEngineReflectionException("Error invoking the method", e, clazz);
                }
            }
        }
    }

    private <T> void parseSpecialRows(List<T> collection, Class<T> clazz) throws ReportEngineReflectionException {
        for(ReportSpecialRow specialRow : reportData.getConfiguration().getSpecialRows()){
            final SpecialDataRow specialDataRow = new SpecialDataRow(specialRow.getRowIndex());
            for (final ReportSpecialRowCell specialRowCell : specialRow.getCells()) {
                if(!specialRowCell.getValueType().equals(ValueType.COLLECTED_VALUE) && !specialRowCell.getValueType().equals(ValueType.COLLECTED_FORMULA_VALUE)) {
                    specialDataRow.addCell(new SpecialDataCell(
                        specialRowCell.getValueType(),
                        specialRowCell.getValue(),
                        specialRowCell.getFormat(),
                        specialRowCell.getTargetId(),
                        specialRowCell.getColumnWidth()
                    ));
                } else if(specialRowCell.getValueType().equals(ValueType.COLLECTED_VALUE) && CollectedValues.class.isAssignableFrom(clazz)){
                    try {
                        final T newInstance = ReflectionUtils.newInstance(clazz);
                        Pair<String, String> pair = Pair.of(reportData.getReportName(), specialRowCell.getValue());
                        if(CollectedValues.class.isAssignableFrom(clazz)){
                            final List<Object> list = new ArrayList<>();
                            for (final T t : collection) {
                                final CollectedValues<?,?> values = (CollectedValues<?,?>) t;
                                if(values.isCollectedValue().get(pair).getAsBoolean()){
                                    list.add(values.getCollectedValue().get(pair));
                                }
                            }
                            final Object value = ((CollectedValues) newInstance).getCollectedValuesResult(list);
                            specialDataRow.addCell(new SpecialDataCell(
                                specialRowCell.getValueType(),
                                value,
                                specialRowCell.getFormat(),
                                specialRowCell.getTargetId()
                            ));
                        }
                    } catch (ReflectiveOperationException e) {
                        throw new ReportEngineReflectionException("Error instantiating an object. The class should have an empty constructor without parameters", e, clazz);
                    }
                } else if(specialRowCell.getValueType().equals(ValueType.COLLECTED_FORMULA_VALUE) && CollectedFormulaValues.class.isAssignableFrom(clazz)) {
                    Pair<String, String> pair = Pair.of(reportData.getReportName(), specialRowCell.getTargetId());
                    Map<String, List<Integer>> valuesById = new HashMap<>();
                    List<T> list = new ArrayList<>(collection);
                    for (int i = 0; i < list.size(); i++) {
                        CollectedFormulaValues collectedFormulaValues = (CollectedFormulaValues) list.get(i);
                        if(collectedFormulaValues.isCollectedFormulaValue().get(pair).getAsBoolean()){
                            if(!valuesById.containsKey(pair.getRight())){
                                valuesById.put(pair.getRight(), new ArrayList<>());
                            }
                            valuesById.get(pair.getRight()).add(i);
                        }
                    }
                    SpecialDataCell specialDataCell = new SpecialDataCell(
                        specialRowCell.getValueType(),
                        specialRowCell.getValue(),
                        specialRowCell.getFormat(),
                        specialRowCell.getTargetId()
                    ).setExtraData(valuesById);

                    specialDataRow.addCell(specialDataCell);
                }
            }
            reportData.addSpecialRow(specialDataRow);
        }
    }

    private <T> void parseStyles(List<T> collection, Class<T> clazz) throws ReportEngineReflectionException {
        super.parseStyles(reportData, clazz);
        try {
            final T newInstance = ReflectionUtils.newInstance(clazz);
            if(newInstance instanceof ConditionalRowStyles){
                final List<T> list = new ArrayList<>(collection);
                final short startRowIndex = reportData.getConfiguration().getDataStartRowIndex();
                RectangleRangedStylesBuilder rectangleRangedStylesBuilder = reportData.getStyles().getRectangleRangedStylesBuilder();
                if(rectangleRangedStylesBuilder == null){
                    rectangleRangedStylesBuilder = reportData.getStyles().createRectangleRangedStylesBuilder(AbstractReportStylesBuilder.StylePriority.PRIORITY4);
                }
                for (int i = 0; i < list.size(); i++) {
                    final T entry = list.get(i);
                    final ConditionalRowStyles conditionalRowStyles = (ConditionalRowStyles) entry;
                    final Optional<Map<String, Predicate<Integer>>> styledOptional = Optional.ofNullable(conditionalRowStyles.isStyled());
                    final Predicate<Integer> predicate = styledOptional
                            .orElseThrow(() -> new ReportEngineRuntimeException("The returned map cannot be null", this.getClass()))
                            .getOrDefault(reportData.getReportName(), null);
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
            this.reportData.getHeader().getCell(entry.getKey()).setValue(entry.getValue());
        }
    }

    ReportData getData(){
        return reportData;
    }

}