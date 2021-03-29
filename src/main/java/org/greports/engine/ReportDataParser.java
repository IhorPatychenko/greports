package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.greports.annotations.Column;
import org.greports.annotations.Subreport;
import org.greports.content.cell.DataCell;
import org.greports.content.cell.HeaderCell;
import org.greports.content.cell.SpecialDataCell;
import org.greports.content.header.ReportHeader;
import org.greports.content.row.DataRow;
import org.greports.content.row.SpecialDataRow;
import org.greports.converters.NotImplementedConverter;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.exceptions.ReportEngineRuntimeException;
import org.greports.interfaces.collectedvalues.CollectedFormulaValues;
import org.greports.interfaces.collectedvalues.CollectedValues;
import org.greports.interfaces.group.GroupedColumns;
import org.greports.interfaces.group.GroupedRows;
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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public final class ReportDataParser<T> extends ReportParser {

    private final LoggerService loggerService;

    private  ReportListDataContainer<T> currentContainer;
    private static final float SUBREPORT_POSITIONAL_INCREMENT = 0.00000000000001f;

    public ReportDataParser(boolean loggerEnabled, Level level) {
        loggerService = LoggerService.forClass(ReportDataParser.class, loggerEnabled, level);
    }

    protected ReportDataParser<T> parse(final Class<T> clazz, final String reportName) throws ReportEngineReflectionException {
        return parse(new ArrayList<>(), reportName, clazz, null);
    }

    protected ReportDataParser<T> parse(List<T> list, final String reportName, final Class<T> clazz, ReportConfigurator configurator) throws ReportEngineReflectionException {
        Utils.validateNotNull(list);

        loggerService.info("Parsing started...");
        loggerService.info(String.format("Parsing report for class \"%s\" with report name \"%s\"...", clazz.getSimpleName(), reportName));
        Stopwatch timer = Stopwatch.createStarted();
        final ReportDataParser<T> parser = parse(list, reportName, clazz, configurator, 0f, "");
        loggerService.info(String.format("Report with name \"%s\" successfully parsed. Parse time: %s", reportName, timer.stop()));
        return parser;
    }

    private ReportDataParser<T> parse(List<T> list, final String reportName, final Class<T> clazz, ReportConfigurator configurator, Float positionIncrement, String idPrefix) throws ReportEngineReflectionException {
        ReportListDataContainer<T> container = new  ReportListDataContainer<>(new ReportData(reportName, ReportConfigurationLoader.load(clazz, reportName)), clazz);

        currentContainer = container;

        final ReportConfiguration configuration = container.getReportData().getConfiguration();

        container.setData(list)
                .setTranslator(new Translator(
                        configuration.getLocale(),
                        configuration.getTranslationsDir(),
                        configuration.getTranslationFileExtension()
                    )
                ).setConfigurator(configurator);

        final ReportData reportData = container.getReportData();

        parseReportHeader(container, positionIncrement, idPrefix);
        parseRowsData(container, positionIncrement);
        parseGroupRows(container);
        parseGroupColumns(container);
        parseSpecialColumns(container);
        parseSpecialRows(container);
        parseStyles(container);
        parseSubreports(container, idPrefix);

        reportData.mergeReportData(container.getSubreportsData());
        reportData.setColumnIndexes();
        reportData.applyConfigurator(configurator);
        return this;
    }

    private void parseReportHeader(final ReportListDataContainer<T> container, Float positionIncrement, String idPrefix) throws ReportEngineReflectionException {
        final ReportData reportData = container.getReportData();
        final Translator translator = container.getTranslator();
        reportData.setCreateHeader(reportData.getConfiguration().isCreateHeader());
        List<HeaderCell> cells = new ArrayList<>();
        final Function<Pair<Method, Column>, Void> columnFunction = AnnotationUtils.getHeadersFunction(cells, translator, positionIncrement, idPrefix);
        AnnotationUtils.methodsWithColumnAnnotations(container.getClazz(), columnFunction, reportData.getReportName());

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

    private void parseRowsData(final ReportListDataContainer<T> container, Float positionIncrement) throws ReportEngineReflectionException {
        final ReportData reportData = container.getReportData();
        reportData.setDataStartRow(reportData.getConfiguration().getDataStartRowIndex());

        Map<Method, Column> columnsMap = new LinkedHashMap<>();
        Function<Pair<Method, Column>, Void> columnFunction = AnnotationUtils.getMethodsAndColumnsFunction(columnsMap);
        AnnotationUtils.methodsWithColumnAnnotations(container.getClazz(), columnFunction, reportData.getReportName());

        final List<T> dataList = container.getData();
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
                    format = container.getConfigurator().getFormatForClass(invokedValue.getClass(), format);
                    if(column.translate() && invokedValue instanceof String) {
                        invokedValue = container.getTranslator().translate(Objects.toString(invokedValue));
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
    }

    private void parseGroupRows(final ReportListDataContainer<T> container) throws ReportEngineReflectionException {
        final List<T> list = container.getData();
        final ReportData reportData = container.getReportData();
        final Class<T> clazz = container.getClazz();
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

    private void parseGroupColumns(final ReportListDataContainer<T> container) throws ReportEngineReflectionException {
        final ReportData reportData = container.getReportData();
        final Class<T> clazz = container.getClazz();
        if(GroupedColumns.class.isAssignableFrom(clazz)){
            final GroupedColumns newInstance = (GroupedColumns) ReflectionUtils.newInstance(clazz);
            if(newInstance.isColumnsCollapsedByDefault() != null && newInstance.isColumnsCollapsedByDefault().containsKey(reportData.getReportName())) {
                final List<Pair<Integer, Integer>> list = newInstance.getColumnGroupRanges().getOrDefault(reportData.getReportName(), new ArrayList<>());
                reportData.setGroupedColumns(list);
            }
        }
    }

    private void parseSubreports(final ReportListDataContainer<T> container, String idPrefix) throws ReportEngineReflectionException {
        final ReportDataParser<T> reportDataParser = new ReportDataParser<>(this.loggerService.isEnabled(), this.loggerService.getLevel());
        Map<Method, Subreport> subreportMap = new LinkedHashMap<>();
        Function<Pair<Method, Subreport>, Void> subreportFunction = AnnotationUtils.getSubreportsFunction(subreportMap);
        AnnotationUtils.methodsWithSubreportAnnotations(container.getClazz(), subreportFunction, container.getReportData().getReportName());

        for (Map.Entry<Method, Subreport> entry : subreportMap.entrySet()) {
            final Method method = entry.getKey();
            final Subreport subreportAnnotation = entry.getValue();
            Class<?> componentType = method.getReturnType();
            method.setAccessible(true);

            if(ReflectionUtils.isListOrArray(componentType)){
                parseIterableSubreports(container, idPrefix, reportDataParser, method, subreportAnnotation, componentType);
            } else {
                parseSubreport(container, subreportAnnotation, reportDataParser, componentType, method, idPrefix);
            }
        }
    }

    private void parseIterableSubreports(final ReportListDataContainer<T> container, String idPrefix, ReportDataParser<?> reportDataParser, Method method, Subreport subreportAnnotation, Class<?> returnType) throws ReportEngineReflectionException {
        Class<?> componentType;
        List<List<?>> subreportsList = new ArrayList<>();
        if(returnType.isArray()){
            componentType = returnType.getComponentType();
        } else {
            ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
            componentType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }
        float subreportPositionalIncrement = Math.max(AnnotationUtils.getSubreportLastColumn(componentType, container.getReportData().getReportName()).position(), SUBREPORT_POSITIONAL_INCREMENT) + SUBREPORT_POSITIONAL_INCREMENT;

        for (T collectionEntry : container.getData()) {
            final Object invokeResult = ReflectionUtils.invokeMethod(method, collectionEntry);
            if(returnType.isArray()){
                subreportsList.add(new ArrayList<>(Arrays.asList((Object[]) invokeResult)));
            } else {
                subreportsList.add((List<?>) invokeResult);
            }
        }

        parseIterableSubreports(container, subreportAnnotation, reportDataParser, componentType, idPrefix, subreportsList, subreportPositionalIncrement);
    }

    private void parseIterableSubreports(final ReportListDataContainer<T> container, Subreport subreportAnnotation, ReportDataParser<?> reportDataParser, Class<?> returnType, String idPrefix, List<List<?>> subreportsList, float subreportPositionalIncrement) throws ReportEngineReflectionException {
        if(!subreportsList.isEmpty()){
            float positionalIncrement = subreportPositionalIncrement;
            final int subreportsInEveryList = subreportsList.stream().map(List::size).max(Integer::compareTo).orElse(0);
            for (int i = 0; i < subreportsInEveryList; i++) {
                final List<Object> subreportData = new ArrayList<>();
                for (final List<?> list : subreportsList) {
                    if(list.size() > i) {
                        subreportData.add(list.get(i));
                    } else {
                        subreportData.add(ReflectionUtils.newInstance(returnType));
                    }
                }
                parseSubreportData(
                    container,
                    reportDataParser,
                    returnType,
                        Utils.generateId(Utils.generateId(idPrefix, subreportAnnotation.id()), Integer.toString(i)), positionalIncrement + subreportAnnotation.position(), subreportData
                );
                positionalIncrement += subreportPositionalIncrement;
            }
        }
    }

    private void parseSubreport(final ReportListDataContainer<T> container, Subreport subreportAnnotation, final ReportDataParser<?> reportDataParser, Class<?> returnType, Method method, String idPrefix) throws ReportEngineReflectionException {
        float subreportPositionalIncrement = AnnotationUtils.getSubreportLastColumn(returnType, container.getReportData().getReportName()).position() + SUBREPORT_POSITIONAL_INCREMENT;
        final List<Object> subreportData = new ArrayList<>();
        for (T entry : container.getData()) {
            final Object invokeResult = ReflectionUtils.invokeMethod(method, entry);
            subreportData.add(invokeResult);
        }
        final float increment = subreportPositionalIncrement + subreportAnnotation.position();
        final String generatedId = Utils.generateId(idPrefix, subreportAnnotation.id());
        parseSubreportData(container, reportDataParser, returnType, generatedId, increment, subreportData);
    }

    @SuppressWarnings("unchecked")
    private void parseSubreportData(final ReportListDataContainer<T> container, final ReportDataParser reportDataParser, final Class<?> returnType, String idfPrefix, float positionalIncrement, final List subreportData) throws ReportEngineReflectionException {
        final ReportData reportData = container.getReportData();
        final ReportConfigurator configurator = container.getConfigurator();
        final ReportDataParser<?> parse = reportDataParser.parse(subreportData, reportData.getReportName(), returnType, configurator.getReportGenerator().getConfigurator(returnType, reportData.getReportName()), positionalIncrement, idfPrefix);
        final ReportData data = parse.getContainer().getReportData();
        container.getSubreportsData().add(data);
    }

    private void parseSpecialColumns(final ReportListDataContainer<T> container) throws ReportEngineReflectionException {
        final List<T> list = container.getData();
        final ReportData reportData = container.getReportData();
        final Class<T> clazz = container.getClazz();
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

    private void parseSpecialRows(final ReportListDataContainer<T> container) throws ReportEngineReflectionException {
        final ReportData reportData = container.getReportData();
        final Class<T> clazz = container.getClazz();
        for(ReportSpecialRow specialRow : reportData.getConfiguration().getSpecialRows()){
            final SpecialDataRow specialDataRow = new SpecialDataRow(specialRow.getRowIndex(), specialRow.isStickyRow());
            for (final ReportSpecialRowCell specialRowCell : specialRow.getCells()) {
                if(!specialRowCell.getValueType().equals(ValueType.COLLECTED_VALUE) && !specialRowCell.getValueType().equals(ValueType.COLLECTED_FORMULA_VALUE)) {
                    specialDataRow.addCell(createSpecialDataCell(container, specialRowCell, specialRowCell.getValue()));
                } else if(specialRowCell.getValueType().equals(ValueType.COLLECTED_VALUE) && CollectedValues.class.isAssignableFrom(clazz)){
                    parseSpecialRowCollectedValue(container, specialDataRow, specialRowCell);
                } else if(specialRowCell.getValueType().equals(ValueType.COLLECTED_FORMULA_VALUE) && CollectedFormulaValues.class.isAssignableFrom(clazz)) {
                    parseSpecialRowCollectedFormulaValue(container, specialDataRow, specialRowCell);
                }
            }
            reportData.addSpecialRow(specialDataRow);
        }
    }

    private void parseSpecialRowCollectedFormulaValue(final ReportListDataContainer<T> container, SpecialDataRow specialDataRow, ReportSpecialRowCell specialRowCell) {
        Pair<String, String> pair = Pair.of(container.getReportData().getReportName(), specialRowCell.getTargetId());
        Map<String, List<Integer>> valuesById = new HashMap<>();
        List<T> list = container.getData();
        for (int i = 0; i < list.size(); i++) {
            CollectedFormulaValues collectedFormulaValues = (CollectedFormulaValues) list.get(i);
            if(collectedFormulaValues.isCollectedFormulaValue().get(pair).getAsBoolean()){
                if(!valuesById.containsKey(pair.getRight())){
                    valuesById.put(pair.getRight(), new ArrayList<>());
                }
                valuesById.get(pair.getRight()).add(i);
            }
        }
        SpecialDataCell specialDataCell = createSpecialDataCell(container, specialRowCell, specialRowCell.getValue()).setValuesById(valuesById);
        specialDataRow.addCell(specialDataCell);
    }

    private void parseSpecialRowCollectedValue(final ReportListDataContainer<T> container, SpecialDataRow specialDataRow, ReportSpecialRowCell specialRowCell) throws ReportEngineReflectionException {
        final Class<T> clazz = container.getClazz();
        final ReportData reportData = container.getReportData();
        final T newInstance = ReflectionUtils.newInstance(clazz);
        Pair<String, String> pair = Pair.of(reportData.getReportName(), specialRowCell.getValue());
        if(CollectedValues.class.isAssignableFrom(clazz)){
            final List<Object> list = new ArrayList<>();
            for (final T t : container.getData()) {
                final CollectedValues<?,?> values = (CollectedValues<?,?>) t;
                if(values.isCollectedValue().get(pair).getAsBoolean()){
                    list.add(values.getCollectedValue().get(pair));
                }
            }
            final Map<Pair<String, String>, Object> value = ((CollectedValues) newInstance).getCollectedValuesResult(list);
            specialDataRow.addCell(createSpecialDataCell(container, specialRowCell, value.get(pair)));
        }
    }

    private static <T> SpecialDataCell createSpecialDataCell(final ReportListDataContainer<T> container, ReportSpecialRowCell specialRowCell, Object value) {
        return new SpecialDataCell(
                specialRowCell.getValueType(),
                value,
                specialRowCell.getFormat(),
                specialRowCell.getTargetId(),
                container.getTranslator().translate(specialRowCell.getComment()),
                specialRowCell.getCommentWidth(),
                specialRowCell.getCommentHeight(),
                specialRowCell.getColumnWidth()
        );
    }

    private void parseStyles(final ReportListDataContainer<T> container) throws ReportEngineReflectionException {
        final ReportData reportData = container.getReportData();
        final Class<T> clazz = container.getClazz();
        final List<T> data = container.getData();
        super.parseStyles(container);
        if(ConditionalRowStyles.class.isAssignableFrom(clazz) || ConditionalCellStyles.class.isAssignableFrom(clazz)){
            final int startRowIndex = reportData.getConfiguration().getDataStartRowIndex();
            ReportStylesBuilder reportStylesBuilder = reportData.getStyles().getReportStylesBuilder();
            if(reportStylesBuilder == null){
                reportStylesBuilder = reportData.getStyles().createReportStylesBuilder();
            }
            for(int i = 0; i < data.size(); i++) {
                final T entry = data.get(i);
                if(ConditionalRowStyles.class.isAssignableFrom(clazz)) {
                    parseConfitionalRowStyles(container, startRowIndex, reportStylesBuilder, i, (ConditionalRowStyles) entry);
                }
                if(ConditionalCellStyles.class.isAssignableFrom(clazz)) {
                    parseConditionalCellStyles(container, startRowIndex, reportStylesBuilder, i, (ConditionalCellStyles) entry);
                }
            }
        }
    }

    private void parseConfitionalRowStyles(final ReportListDataContainer<T> container, int startRowIndex, ReportStylesBuilder reportStylesBuilder, int i, ConditionalRowStyles entry) {
        final ReportData reportData = container.getReportData();
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

    private void parseConditionalCellStyles(final ReportListDataContainer<T> container, int startRowIndex, ReportStylesBuilder reportStylesBuilder, int i, ConditionalCellStyles entry) {
        final ReportData reportData = container.getReportData();
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

     ReportListDataContainer<T> getContainer() {
        return currentContainer;
    }
}