package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.greports.annotations.Column;
import org.greports.annotations.Subreport;
import org.greports.content.ReportHeader;
import org.greports.content.cell.DataCell;
import org.greports.content.cell.HeaderCell;
import org.greports.content.cell.SpecialDataCell;
import org.greports.content.row.DataRow;
import org.greports.content.row.SpecialDataRow;
import org.greports.converters.NotImplementedConverter;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.exceptions.ReportEngineRuntimeException;
import org.greports.interfaces.CollectedFormulaValues;
import org.greports.interfaces.CollectedValues;
import org.greports.interfaces.GroupedColumns;
import org.greports.interfaces.GroupedRows;
import org.greports.positioning.HorizontalRange;
import org.greports.positioning.Position;
import org.greports.positioning.VerticalRange;
import org.greports.services.LoggerService;
import org.greports.styles.interfaces.ConditionalCellStyles;
import org.greports.styles.interfaces.ConditionalRowStyles;
import org.greports.styles.stylesbuilders.ReportStyleBuilder;
import org.greports.styles.stylesbuilders.ReportStylesBuilder;
import org.greports.utils.AnnotationUtils;
import org.greports.utils.ConverterUtils;
import org.greports.utils.ReflectionUtils;
import org.greports.utils.TranslationsParser;
import org.greports.utils.Translator;
import org.greports.utils.Utils;

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
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public final class ReportDataParser extends ReportParser {

    private final LoggerService loggerService;

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

    protected <T> ReportDataParser parse(final Class<T> clazz, final String reportName, List<T> collection, ReportConfigurator configurator) throws ReportEngineReflectionException {
        loggerService.info("Parsing started...");
        loggerService.info(String.format("Parsing report for class \"%s\" with report name \"%s\"...", clazz.getSimpleName(), reportName));
        Stopwatch timer = Stopwatch.createStarted();
        final ReportDataParser parser = parse(clazz, reportName, collection, configurator, 0f, "");
        reportData.setColumnIndexes();
        reportData.applyConfigurator(configurator);
        loggerService.info(String.format("Report with name \"%s\" successfully parsed. Parse time: %s", reportName, timer.stop()));
        return parser;
    }

    private <T> ReportDataParser parse(final Class<T> clazz, final String reportName, List<T> collection, ReportConfigurator configurator, Float positionIncrement, String idPrefix) throws ReportEngineReflectionException {
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
            if(!StringUtils.EMPTY.equals(idPrefix)) {
                generateIdPrefix = Utils.generateId(generateIdPrefix, Integer.toString(i));
            }
            cells.add(new HeaderCell(specialColumn, generateIdPrefix));
        }

        final ReportHeader reportHeader = new ReportHeader(
                reportData.getConfiguration().isSortableHeader(),
                reportData.getConfiguration().isStickyHeader(),
                reportData.getConfiguration().getHeaderRowIndex()
        ).addCells(cells);

        reportData.setHeader(reportHeader);
        reportData.setTargetIds();
    }

    private <T> void parseRowsData(Class<T> clazz, List<T> collection, Float positionIncrement) throws ReportEngineReflectionException {
        reportData.setDataStartRow(reportData.getConfiguration().getDataStartRowIndex());

        Map<Method, Column> columnsMap = new LinkedHashMap<>();
        Function<Pair<Method, Column>, Void> columnFunction = AnnotationUtils.getMethodsAndColumnsFunction(columnsMap);
        AnnotationUtils.methodsWithColumnAnnotations(clazz, columnFunction, reportData.getReportName());

        List<T> dataList = new ArrayList<>(collection);
        for (int i = 0; i < dataList.size(); i++) {
            T dto = dataList.get(i);
            DataRow row = new DataRow(reportData.getConfiguration().getDataStartRowIndex() + i);
            for (final Map.Entry<Method, Column> entry : columnsMap.entrySet()) {
                final Column column = entry.getValue();
                final Method method = entry.getKey();
                method.setAccessible(true);
                Object invokedValue = dto != null ? ReflectionUtils.invokeMethod(method, dto) : null;

                if(!column.getterConverter().converterClass().equals(NotImplementedConverter.class)){
                    invokedValue = ConverterUtils.convertValue(invokedValue, column.getterConverter());
                }

                String format = column.format();

                if(invokedValue != null) {
                    format = reportConfigurator.getFormatForClass(invokedValue.getClass(), format);
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
    }

    private <T> void parseGroupRows(final Class<T> clazz, final List<T> list) throws ReportEngineReflectionException {
        if(GroupedRows.class.isAssignableFrom(clazz)){
            final GroupedRows newInstance = (GroupedRows) ReflectionUtils.newInstance(clazz);
            if(newInstance.isRowCollapsedByDefault() != null && newInstance.isRowCollapsedByDefault().containsKey(reportData.getReportName())){
                reportData.setGroupedRowsDefaultCollapsed(newInstance.isRowCollapsedByDefault().get(reportData.getReportName()).getAsBoolean());
                Integer groupStart = null;
                for(int i = 0; i < list.size(); i++) {
                    GroupedRows groupedRows = (GroupedRows) list.get(i);
                    if(groupedRows.isGroupStartRow().get(reportData.getReportName()).test(i)){
                        groupStart = i;
                    }
                    if(groupedRows.isGroupEndRow().get(reportData.getReportName()).test(i)) {
                        reportData.addGroupedRow(Pair.of(groupStart, i));
                    }
                }
            }
        }
    }

    private <T> void parseGroupColumns(final Class<T> clazz) throws ReportEngineReflectionException {
        if(GroupedColumns.class.isAssignableFrom(clazz)){
            final GroupedColumns newInstance = (GroupedColumns) ReflectionUtils.newInstance(clazz);
            if(newInstance.isColumnsCollapsedByDefault() != null && newInstance.isColumnsCollapsedByDefault().containsKey(reportData.getReportName())) {
                final List<Pair<Integer, Integer>> list = newInstance.getColumnGroupRanges().getOrDefault(reportData.getReportName(), new ArrayList<>());
                reportData.setGroupedColumns(list);
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
            final Subreport subreportAnnotation = entry.getValue();
            Class<?> componentType = method.getReturnType();
            method.setAccessible(true);

            if(ReflectionUtils.isListOrArray(componentType)){
                parseIterableSubreports(collection, idPrefix, reportDataParser, method, subreportAnnotation, componentType);
            } else {
                parseSubreport(collection, idPrefix, reportDataParser, method, subreportAnnotation, componentType);
            }
        }
    }

    private <T> void parseIterableSubreports(List<T> collection, String idPrefix, ReportDataParser reportDataParser, Method method, Subreport subreportAnnotation, Class<?> returnType) throws ReportEngineReflectionException {
        Class<?> componentType;
        List<List<?>> subreportsList = new ArrayList<>();
        if(returnType.isArray()){
            componentType = returnType.getComponentType();
        } else {
            ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
            componentType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }
        float subreportPositionalIncrement = Math.max(AnnotationUtils.getSubreportLastColumn(componentType, reportData.getReportName()).position(), SUBREPORT_POSITIONAL_INCREMENT) + SUBREPORT_POSITIONAL_INCREMENT;

        for (T collectionEntry : collection) {
            final Object invokeResult = ReflectionUtils.invokeMethod(method, collectionEntry);
            if(returnType.isArray()){
                subreportsList.add(new ArrayList<>(Arrays.asList((Object[]) invokeResult)));
            } else {
                subreportsList.add((List<?>) invokeResult);
            }
        }

        parseIterableSubreports(idPrefix, reportDataParser, subreportAnnotation, componentType, subreportsList, subreportPositionalIncrement);
    }

    private void parseIterableSubreports(String idPrefix, ReportDataParser reportDataParser, Subreport subreportAnnotation, Class<?> componentType, List<List<?>> subreportsList, float subreportPositionalIncrement) throws ReportEngineReflectionException {
        if(!subreportsList.isEmpty()){
            float positionalIncrement = subreportPositionalIncrement;
            final int subreportsInEveryList = subreportsList.stream().map(List::size).max(Integer::compareTo).orElse(0);
            for (int i = 0; i < subreportsInEveryList; i++) {
                final List<Object> subreportData = new ArrayList<>();
                for (final List<?> list : subreportsList) {
                    if(list.size() > i) {
                        subreportData.add(list.get(i));
                    } else {
                        subreportData.add(ReflectionUtils.newInstance(componentType));
                    }
                }
                parseSubreportData(
                    reportDataParser,
                    componentType,
                    subreportData,
                    positionalIncrement + subreportAnnotation.position(),
                    Utils.generateId(Utils.generateId(idPrefix, subreportAnnotation.id()), Integer.toString(i))
                );
                positionalIncrement += subreportPositionalIncrement;
            }
        }
    }

    private <T> void parseSubreport(List<T> collection, String idPrefix, ReportDataParser reportDataParser, Method method, Subreport subreportAnnotation, Class<?> returnType) throws ReportEngineReflectionException {
        float subreportPositionalIncrement = AnnotationUtils.getSubreportLastColumn(returnType, reportData.getReportName()).position() + SUBREPORT_POSITIONAL_INCREMENT;
        final List<Object> subreportData = new ArrayList<>();
        for (T collectionEntry : collection) {
            final Object invokeResult = ReflectionUtils.invokeMethod(method, collectionEntry);
            subreportData.add(invokeResult);
        }
        parseSubreportData(reportDataParser, returnType, subreportData, subreportPositionalIncrement + subreportAnnotation.position(), Utils.generateId(idPrefix, subreportAnnotation.id()));
    }

    @SuppressWarnings("unchecked")
    private void parseSubreportData(final ReportDataParser reportDataParser, final Class<?> returnType, final List subreportData, float positionalIncrement, String idfPrefix) throws ReportEngineReflectionException {
        final ReportData data = reportDataParser.parse(returnType, reportData.getReportName(), subreportData, reportConfigurator.getReportGenerator().getConfigurator(returnType, reportData.getReportName()), positionalIncrement, idfPrefix).getData();
        subreportsData.add(data);
    }

    private <T> void parseSpecialColumns(final Class<T> clazz, List<T> collection) throws ReportEngineReflectionException {
        List<T> list = new ArrayList<>(collection);
        for (ReportSpecialColumn specialColumn : reportData.getConfiguration().getSpecialColumns()) {
            Method method = null;
            if(ValueType.METHOD.equals(specialColumn.getValueType())){
                method = MethodUtils.getMatchingMethod(clazz, specialColumn.getValue());
            }
            for (int i = 0; i < list.size(); i++) {
                Object value = specialColumn.getValue();
                if(method != null) {
                    final T listElement = list.get(i);
                    method.setAccessible(true);
                    value = ReflectionUtils.invokeMethod(method, listElement);
                }
                reportData.getDataRows().get(i).addCell(new DataCell(
                        specialColumn.getPosition(),
                        false,
                        specialColumn.getFormat(),
                        value,
                        specialColumn.getValueType(),
                        specialColumn.getColumnWidth()
                ));
            }
        }
    }

    private <T> void parseSpecialRows(final Class<T> clazz, List<T> collection) throws ReportEngineReflectionException {
        for(ReportSpecialRow specialRow : reportData.getConfiguration().getSpecialRows()){
            final SpecialDataRow specialDataRow = new SpecialDataRow(specialRow.getRowIndex(), specialRow.isStickyRow());
            for (final ReportSpecialRowCell specialRowCell : specialRow.getCells()) {
                if(!specialRowCell.getValueType().equals(ValueType.COLLECTED_VALUE) && !specialRowCell.getValueType().equals(ValueType.COLLECTED_FORMULA_VALUE)) {
                    specialDataRow.addCell(createSpecialDataCell(specialRowCell, specialRowCell.getValue()));
                } else if(specialRowCell.getValueType().equals(ValueType.COLLECTED_VALUE) && CollectedValues.class.isAssignableFrom(clazz)){
                    parseSpecialRowCollectedValue(clazz, collection, specialDataRow, specialRowCell);
                } else if(specialRowCell.getValueType().equals(ValueType.COLLECTED_FORMULA_VALUE) && CollectedFormulaValues.class.isAssignableFrom(clazz)) {
                    parseSpecialRowCollectedFormulaValue(collection, specialDataRow, specialRowCell);
                }
            }
            reportData.addSpecialRow(specialDataRow);
        }
    }

    private <T> void parseSpecialRowCollectedFormulaValue(List<T> collection, SpecialDataRow specialDataRow, ReportSpecialRowCell specialRowCell) {
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
        ).setValuesById(valuesById);

        specialDataRow.addCell(specialDataCell);
    }

    private <T> void parseSpecialRowCollectedValue(Class<T> clazz, List<T> collection, SpecialDataRow specialDataRow, ReportSpecialRowCell specialRowCell) throws ReportEngineReflectionException {
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
            final Map<Pair<String, String>, Object> value = ((CollectedValues) newInstance).getCollectedValuesResult(list);
            specialDataRow.addCell(createSpecialDataCell(specialRowCell, value.get(pair)));
        }
    }

    private static SpecialDataCell createSpecialDataCell(ReportSpecialRowCell specialRowCell, Object value) {
        return new SpecialDataCell(
                specialRowCell.getValueType(),
                value,
                specialRowCell.getFormat(),
                specialRowCell.getTargetId(),
                specialRowCell.getColumnWidth()
        );
    }

    private <T> void parseStyles(Class<T> clazz, List<T> collection) throws ReportEngineReflectionException {
        super.parseStyles(reportData, clazz);
        if(ConditionalRowStyles.class.isAssignableFrom(clazz) || ConditionalCellStyles.class.isAssignableFrom(clazz)){
            final List<T> list = new ArrayList<>(collection);
            final int startRowIndex = reportData.getConfiguration().getDataStartRowIndex();
            ReportStylesBuilder reportStylesBuilder = reportData.getStyles().getReportStylesBuilder();
            if(reportStylesBuilder == null){
                reportStylesBuilder = reportData.getStyles().createReportStylesBuilder();
            }
            for(int i = 0; i < list.size(); i++) {
                final T entry = list.get(i);
                if(ConditionalRowStyles.class.isAssignableFrom(clazz)) {
                    parseConfitionalRowStyles(startRowIndex, reportStylesBuilder, i, (ConditionalRowStyles) entry);
                }
                if(ConditionalCellStyles.class.isAssignableFrom(clazz)) {
                    parseConditionalCellStyles(startRowIndex, reportStylesBuilder, i, (ConditionalCellStyles) entry);
                }
            }
        }
    }

    private void parseConfitionalRowStyles(int startRowIndex, ReportStylesBuilder reportStylesBuilder, int i, ConditionalRowStyles entry) {
        final Optional<Map<String, IntPredicate>> styledOptional = Optional.ofNullable(entry.isStyled());
        final List<ReportStyleBuilder<HorizontalRange>> horizontalRangedStyleBuilders = entry.getIndexBasedStyle().getOrDefault(reportData.getReportName(), new ArrayList<>());
        final IntPredicate predicate = styledOptional
                .orElseThrow(() -> new ReportEngineRuntimeException("The returned map cannot be null", this.getClass()))
                .getOrDefault(reportData.getReportName(), null);
        if(predicate != null && predicate.test(i)) {
            for(ReportStyleBuilder<HorizontalRange> styleBuilder : horizontalRangedStyleBuilders) {
                reportStylesBuilder.addStyleBuilder(new ReportStyleBuilder<>(new VerticalRange(startRowIndex + i, startRowIndex + i), styleBuilder.toRectangeRangeStyleBuilder()));
            }
        }
    }

    private void parseConditionalCellStyles(int startRowIndex, ReportStylesBuilder reportStylesBuilder, int i, ConditionalCellStyles entry) {
        final Optional<Map<String, List<Pair<String, Predicate<Integer>>>>> styledOptional = Optional.ofNullable(entry.isCellStyled());
        final List<Pair<String, Predicate<Integer>>> predicatePairs = styledOptional
                .orElseThrow(() -> new ReportEngineRuntimeException("The returned map cannot be null", this.getClass()))
                .getOrDefault(reportData.getReportName(), null);
        final List<Pair<String, ReportStyleBuilder<Position>>> styleBuilders = entry.getIndexBasedCellStyle().getOrDefault(reportData.getReportName(), null);
        for(Pair<String, Predicate<Integer>> predicatePair : predicatePairs) {
            if(predicatePair.getRight() != null && predicatePair.getRight().test(i)) {
                for(Pair<String, ReportStyleBuilder<Position>> styleBuilderPair : styleBuilders) {
                    if(styleBuilderPair.getLeft().equals(predicatePair.getLeft())) {
                        final ReportStyleBuilder<Position> positionedStyleBuilder = styleBuilderPair.getRight();
                        final Position position = new Position(startRowIndex + i, reportData.getColumnIndexForId(styleBuilderPair.getLeft()));
                        reportStylesBuilder.addStyleBuilder(new ReportStyleBuilder<>(position, positionedStyleBuilder));
                    }
                }
            }
        }
    }

    ReportData getData(){
        return reportData;
    }

}