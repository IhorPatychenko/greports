package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.apache.log4j.Level;
import org.greports.annotations.Column;
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
import org.greports.interfaces.GroupedColumns;
import org.greports.interfaces.GroupedRows;
import org.greports.positioning.TranslationsParser;
import org.greports.services.LoggerService;
import org.greports.styles.interfaces.ConditionalCellStyles;
import org.greports.styles.interfaces.ConditionalRowStyles;
import org.greports.styles.stylesbuilders.AbstractReportStylesBuilder;
import org.greports.styles.stylesbuilders.HorizontalRangedStyleBuilder;
import org.greports.styles.stylesbuilders.PositionedStyleBuilder;
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

public final class ReportDataParser extends ReportParser {

    private LoggerService loggerService;

    private ReportData reportData;
    private Translator translator;
    private ReportConfigurator reportConfigurator;
    private List<ReportData> subreportsData = new ArrayList<>();
    private static final float SUBREPORT_POSITIONAL_INCREMENT = 0.00000000000001f;

    public ReportDataParser(boolean loggerEnabled, Level level) {
        loggerService = LoggerService.forClass(ReportDataParser.class, loggerEnabled, level);
    }

    protected <T> ReportDataParser parse(final Class<T> clazz, final String reportName) throws ReportEngineReflectionException {
        return parse(clazz, reportName, new ArrayList<>(), null);
    }

    protected <T> ReportDataParser parse(final Class<T> clazz, final String reportName, List<T> collection, ReportConfigurator configurator) throws ReportEngineReflectionException, ReportEngineRuntimeException {
        loggerService.info("Parsing started...");
        loggerService.info(String.format("Parsing report for class \"%s\" with report name \"%s\"...", clazz.getSimpleName(), reportName));
        Stopwatch timer = Stopwatch.createStarted();
        final ReportDataParser parser = parse(clazz, reportName, collection, configurator, 0f, "");
        reportData.setColumnIndexes();
        reportData.applyConfigurator(configurator);
        loggerService.info(String.format("Report with name \"%s\" successfully parsed. Parse time: %s", reportName, timer.stop()));
        return parser;
    }

    private <T> ReportDataParser parse(final Class<T> clazz, final String reportName, List<T> collection, ReportConfigurator configurator, Float positionIncrement, String idPrefix) throws ReportEngineReflectionException, ReportEngineRuntimeException {
        reportData = new ReportData(reportName, ReportConfigurationLoader.load(clazz, reportName));
        final Map<String, Object> translations = new TranslationsParser(reportData.getConfiguration()).getTranslations();
        translator = new Translator(translations);
        subreportsData = new ArrayList<>();
        reportConfigurator = configurator;
        parseReportHeader(clazz, positionIncrement, idPrefix);
        parseRowsData(clazz, collection, positionIncrement);
        parseGroupRows(clazz, collection);
        parseGroupColumns(clazz);
        parseSpecialColumns(clazz, collection);
        parseSpecialRows(clazz, collection);
        parseStyles(clazz, collection);
        parseSubreports(clazz, collection, idPrefix);
        reportData.mergeReportData(subreportsData);
        return this;
    }

    private <T> void parseReportHeader(final Class<T> clazz, Float positionIncrement, String idPrefix) throws ReportEngineReflectionException {
        reportData.setCreateHeader(reportData.getConfiguration().isCreateHeader());
        List<HeaderCell> cells = new ArrayList<>();
        final Function<Pair<Method, Column>, Void> columnFunction = AnnotationUtils.getHeadersFunction(cells, translator, positionIncrement, idPrefix);
        AnnotationUtils.methodsWithColumnAnnotations(clazz, columnFunction, reportData.getReportName());

        final List<ReportSpecialColumn> specialColumns = reportData.getConfiguration().getSpecialColumns();
        for(int i = 0; i < specialColumns.size(); i++) {
            final ReportSpecialColumn specialColumn = specialColumns.get(i);
            String generateIdPrefix = Utils.generateId(idPrefix, specialColumn.getTitle());
            if(!"".equals(idPrefix)) {
                generateIdPrefix = Utils.generateId(generateIdPrefix, Integer.toString(i));
            }
            cells.add(new HeaderCell(
                    specialColumn.getPosition(),
                    generateIdPrefix,
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

        reportData.setTargetIds();
    }

    private <T> void parseRowsData(Class<T> clazz, List<T> collection, Float positionIncrement) throws ReportEngineReflectionException {
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

    private <T> void parseGroupRows(final Class<T> clazz, final List<T> list) throws ReportEngineReflectionException {
        if(GroupedRows.class.isAssignableFrom(clazz)){
            try {
                final GroupedRows newInstance = (GroupedRows) ReflectionUtils.newInstance(clazz);
                if(newInstance.isRowCollapsedByDefault() != null && newInstance.isRowCollapsedByDefault().containsKey(reportData.getReportName())){
                    reportData.setGroupedRowsDefaultCollapsed(newInstance.isRowCollapsedByDefault().get(reportData.getReportName()).getAsBoolean());
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
                }
            } catch (ReflectiveOperationException e) {
                throw new ReportEngineReflectionException("Error instantiating an object. The class should have an empty constructor without parameters", e, clazz);
            }
        }
    }


    private <T> void parseGroupColumns(final Class<T> clazz) throws ReportEngineReflectionException {
        if(GroupedColumns.class.isAssignableFrom(clazz)){
            try {
                final GroupedColumns newInstance = (GroupedColumns) ReflectionUtils.newInstance(clazz);
                if(newInstance.isColumnsCollapsedByDefault() != null && newInstance.isColumnsCollapsedByDefault().containsKey(reportData.getReportName())) {
                    final List<Pair<Integer, Integer>> list = newInstance.getColumnGroupRanges().getOrDefault(reportData.getReportName(), new ArrayList<>());
                    reportData.setGroupedColumns(list);
                }
            } catch (ReflectiveOperationException e) {
                throw new ReportEngineReflectionException("Error instantiating an object. The class should have an empty constructor without parameters", e, clazz);
            }
        }
    }

    private <T> void parseSubreports(Class<T> clazz, List<T> collection, String idPrefix) throws ReportEngineReflectionException {
        final ReportDataParser reportDataParser = new ReportDataParser(this.loggerService.isEnabled(), this.loggerService.getLevel());
        Map<Method, Subreport> subreportMap = new LinkedHashMap<>();
        Function<Pair<Method, Subreport>, Void> subreportFunction = AnnotationUtils.getSubreportsFunction(subreportMap);
        AnnotationUtils.methodsWithSubreportAnnotations(clazz, subreportFunction, reportData.getReportName());

        for (Map.Entry<Method, Subreport> entry : subreportMap.entrySet()) {
            final Method method = entry.getKey();
            final Subreport subreport = entry.getValue();
            Class<?> returnType = method.getReturnType();
            Class<?> componentType = returnType;
            method.setAccessible(true);

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
                        parseSubreportData(
                                reportDataParser,
                                componentType,
                                subreportData,
                                positionalIncrement + subreport.position(),
                                Utils.generateId(Utils.generateId(idPrefix, subreport.id()), Integer.toString(i))
                        );
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
                parseSubreportData(reportDataParser, returnType, subreportData, subreportPositionalIncrement + subreport.position(), Utils.generateId(idPrefix, subreport.id()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void parseSubreportData(final ReportDataParser reportDataParser, final Class<?> returnType, final List subreportData, float positionalIncrement, String idfPrefix) throws ReportEngineReflectionException {
        final ReportData data = reportDataParser.parse(returnType, reportData.getReportName(), subreportData, reportConfigurator.getReportGenerator().getConfigurator(returnType, reportData.getReportName()), positionalIncrement, idfPrefix).getData();
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

    private <T> void parseSpecialColumns(final Class<T> clazz, List<T> collection) throws ReportEngineReflectionException {
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

    private <T> void parseSpecialRows(final Class<T> clazz, List<T> collection) throws ReportEngineReflectionException {
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

    private <T> void parseStyles(Class<T> clazz, List<T> collection) throws ReportEngineReflectionException {
        super.parseStyles(reportData, clazz);
        try {
            final T newInstance = ReflectionUtils.newInstance(clazz);
            if(newInstance instanceof ConditionalRowStyles || newInstance instanceof ConditionalCellStyles){
                final List<T> list = new ArrayList<>(collection);
                final int startRowIndex = reportData.getConfiguration().getDataStartRowIndex();
                RectangleRangedStylesBuilder rectangleRangedStylesBuilder = reportData.getStyles().getRectangleRangedStylesBuilder();
                if(rectangleRangedStylesBuilder == null){
                    rectangleRangedStylesBuilder = reportData.getStyles().createRectangleRangedStylesBuilder(AbstractReportStylesBuilder.StylePriority.PRIORITY4);
                }
                for(int i = 0; i < list.size(); i++) {
                    final T entry = list.get(i);
                    if(newInstance instanceof ConditionalRowStyles) {
                        final ConditionalRowStyles conditionalRowStyles = (ConditionalRowStyles) entry;
                        final Optional<Map<String, Predicate<Integer>>> styledOptional = Optional.ofNullable(conditionalRowStyles.isStyled());
                        final List<HorizontalRangedStyleBuilder> horizontalRangedStyleBuilders = conditionalRowStyles.getIndexBasedStyle().getOrDefault(reportData.getReportName(), new ArrayList<>());
                        final Predicate<Integer> predicate = styledOptional
                                .orElseThrow(() -> new ReportEngineRuntimeException("The returned map cannot be null", this.getClass()))
                                .getOrDefault(reportData.getReportName(), null);
                        if(predicate != null && predicate.test(i)) {
                            for(final HorizontalRangedStyleBuilder styleBuilder : horizontalRangedStyleBuilders) {
                                final RectangleRangedStyleBuilder rectangleRangedStyleBuilder = new RectangleRangedStyleBuilder(styleBuilder, startRowIndex + i);
                                rectangleRangedStylesBuilder.addStyleBuilder(rectangleRangedStyleBuilder);
                            }
                        }
                    }
                    if(newInstance instanceof ConditionalCellStyles){
                        final ConditionalCellStyles conditionalCellStyles = (ConditionalCellStyles) entry;
                        final Optional<Map<String, List<Pair<String, Predicate<Integer>>>>> styledOptional = Optional.ofNullable(conditionalCellStyles.isCellStyled());
                        final List<Pair<String, Predicate<Integer>>> predicatePairs = styledOptional
                                .orElseThrow(() -> new ReportEngineRuntimeException("The returned map cannot be null", this.getClass()))
                                .getOrDefault(reportData.getReportName(), null);
                        final List<Pair<String, PositionedStyleBuilder>> styleBuilders = ((ConditionalCellStyles) entry).getIndexBasedCellStyle().getOrDefault(reportData.getReportName(), null);
                        for(Pair<String, Predicate<Integer>> predicatePair : predicatePairs) {
                            if(predicatePair.getRight() != null && predicatePair.getRight().test(i)) {
                                for(Pair<String, PositionedStyleBuilder> styleBuilderPair : styleBuilders) {
                                    if(styleBuilderPair.getLeft().equals(predicatePair.getLeft())) {
                                        final PositionedStyleBuilder positionedStyleBuilder = styleBuilderPair.getRight();
                                        positionedStyleBuilder.getTuple()
                                                .setRow(startRowIndex + i)
                                                .setColumn(reportData.getColumnIndexForId(styleBuilderPair.getLeft()));
                                        final RectangleRangedStyleBuilder rectangleRangedStyleBuilder = new RectangleRangedStyleBuilder(positionedStyleBuilder);
                                        rectangleRangedStylesBuilder.addStyleBuilder(rectangleRangedStyleBuilder);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new ReportEngineReflectionException("Error instantiating an object. The class should have an empty constructor without parameters", e, clazz);
        }
    }

    ReportData getData(){
        return reportData;
    }

}