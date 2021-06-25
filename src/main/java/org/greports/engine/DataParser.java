package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.greports.annotations.Column;
import org.greports.annotations.SpecialRow;
import org.greports.annotations.SpecialRowCell;
import org.greports.annotations.Subreport;
import org.greports.content.cell.DataCell;
import org.greports.content.cell.HeaderCell;
import org.greports.content.cell.SpecialDataRowCell;
import org.greports.content.header.ReportHeader;
import org.greports.content.row.DataRow;
import org.greports.content.row.SpecialDataRow;
import org.greports.converters.NotImplementedConverter;
import org.greports.exceptions.GreportsReflectionException;
import org.greports.exceptions.GreportsRuntimeException;
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
import org.greports.utils.Translator;
import org.greports.utils.Utils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public final class DataParser<T> extends Parser {

    private final LoggerService loggerService;

    private ListDataContainer<T> currentContainer;
    private static final float SUBREPORT_POSITIONAL_INCREMENT = 0.00000000000001f;

    public DataParser(boolean loggerEnabled, Level level) {
        loggerService = LoggerService.forClass(DataParser.class, loggerEnabled, level);
    }

    protected DataParser<T> parse(final Class<T> clazz, final String reportName) throws GreportsReflectionException {
        return parse(new ArrayList<>(), reportName, clazz, null);
    }

    protected DataParser<T> parse(List<T> list, final String reportName, final Class<T> clazz, Configurator configurator) throws GreportsReflectionException {
        Utils.validateNotNull(list);

        loggerService.info("Parsing started...");
        loggerService.info(String.format("Parsing report for class \"%s\" with report name \"%s\"...", clazz.getSimpleName(), reportName));
        Stopwatch timer = Stopwatch.createStarted();
        final DataParser<T> parser = parse(list, reportName, clazz, configurator, 0f, StringUtils.EMPTY);
        loggerService.info(String.format("Report with name \"%s\" successfully parsed. Parse time: %s", reportName, timer.stop()));
        return parser;
    }

    private DataParser<T> parse(List<T> list, final String reportName, final Class<T> clazz, Configurator configurator, Float positionIncrement, String idPrefix) throws GreportsReflectionException {
        ListDataContainer<T> container = new ListDataContainer<>(new Data(reportName, Configuration.load(clazz, reportName)), clazz);
        final Configuration configuration = container.getReportData().getConfiguration();
        final Translator translator = new Translator(configuration);

        container.setData(list)
                .setTranslator(translator)
                .setConfigurator(configurator);

        currentContainer = container;

        final Data data = container.getReportData();

        parseReportHeader(container, positionIncrement, idPrefix);
        parseRowsData(container, positionIncrement);
        parseGroupRows(container);
        parseGroupColumns(container);
        parseSpecialColumns(container);
        parseSpecialRows(container);
        parseStyles(container);
        parseSubreports(container, idPrefix);

        data.mergeReportData(container.getSubreportsData());
        data.setColumnIndexes();
        data.applyConfigurator(configurator);
        return this;
    }

    private void parseReportHeader(final ListDataContainer<T> container, Float positionIncrement, String idPrefix) throws GreportsReflectionException {
        final Data data = container.getReportData();
        final Translator translator = container.getTranslator();
        final Configuration configuration = data.getConfiguration();
        data.setCreateHeader(configuration.isCreateHeader());
        List<HeaderCell> cells = new ArrayList<>();
        final Function<Pair<Column, Method>, Void> columnFunction = AnnotationUtils.getHeadersFunction(cells, translator, positionIncrement, idPrefix);
        AnnotationUtils.methodsWithColumnAnnotations(container.getClazz(), columnFunction, data.getReportName());

        final List<SpecialColumn> specialColumns = configuration.getSpecialColumns();
        for(int i = 0; i < specialColumns.size(); i++) {
            final SpecialColumn specialColumn = specialColumns.get(i);
            String generateIdPrefix = Utils.generateId(idPrefix, specialColumn.getTitle());
            if(!StringUtils.EMPTY.equals(idPrefix)) {
                generateIdPrefix = Utils.generateId(generateIdPrefix, Integer.toString(i));
            }
            cells.add(new HeaderCell(specialColumn, generateIdPrefix));
        }

        final ReportHeader reportHeader = new ReportHeader(configuration).addCells(cells);

        data.setHeader(reportHeader);
        data.setTargetIds();
    }

    private void parseRowsData(final ListDataContainer<T> container, Float positionIncrement) throws GreportsReflectionException {
        final Data data = container.getReportData();
        data.setDataStartRow(data.getConfiguration().getDataStartRowIndex());

        Map<Column, Method> columnsMap = new LinkedHashMap<>();
        Function<Pair<Column, Method>, Void> columnFunction = AnnotationUtils.getMethodsAndColumnsFunction(columnsMap);
        AnnotationUtils.methodsWithColumnAnnotations(container.getClazz(), columnFunction, data.getReportName());

        final List<T> dataList = container.getData();
        for (int i = 0; i < dataList.size(); i++) {
            T dto = dataList.get(i);
            DataRow row = new DataRow(data.getConfiguration().getDataStartRowIndex() + i);
            for (final Map.Entry<Column, Method> entry : columnsMap.entrySet()) {
                final Column column = entry.getKey();
                Method method = entry.getValue();
                method.setAccessible(true);

                Object invokedValue = super.checkNestedValue(dto, method, AnnotationUtils.hasNestedTarget(column), column.target());

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
            data.addRow(row);
        }
    }

    private void parseGroupRows(final ListDataContainer<T> container) throws GreportsReflectionException {
        final List<T> list = container.getData();
        final Data data = container.getReportData();
        final Class<T> clazz = container.getClazz();
        if(GroupedRows.class.isAssignableFrom(clazz)){
            final GroupedRows newInstance = (GroupedRows) ReflectionUtils.newInstance(clazz);
            if(newInstance.isRowCollapsedByDefault() != null && newInstance.isRowCollapsedByDefault().containsKey(data.getReportName())){
                data.setGroupedRowsDefaultCollapsed(newInstance.isRowCollapsedByDefault().get(data.getReportName()).getAsBoolean());
                Integer groupStart = null;
                for(int i = 0; i < list.size(); i++) {
                    GroupedRows groupedRows = (GroupedRows) list.get(i);
                    if(groupedRows.isGroupStartRow().get(data.getReportName()).test(i)){
                        groupStart = i;
                    }
                    if(groupedRows.isGroupEndRow().get(data.getReportName()).test(i)) {
                        data.addGroupedRow(Pair.of(groupStart, i));
                    }
                }
            }
        }
    }

    private void parseGroupColumns(final ListDataContainer<T> container) throws GreportsReflectionException {
        final Data data = container.getReportData();
        final Class<T> clazz = container.getClazz();
        if(GroupedColumns.class.isAssignableFrom(clazz)){
            final GroupedColumns newInstance = (GroupedColumns) ReflectionUtils.newInstance(clazz);
            if(newInstance.isColumnsCollapsedByDefault() != null && newInstance.isColumnsCollapsedByDefault().containsKey(data.getReportName())) {
                final List<Pair<Integer, Integer>> list = newInstance.getColumnGroupRanges().getOrDefault(data.getReportName(), new ArrayList<>());
                data.setGroupedColumns(list);
            }
        }
    }

    private void parseSubreports(final ListDataContainer<T> container, String idPrefix) throws GreportsReflectionException {
        final DataParser<T> dataParser = new DataParser<>(this.loggerService.isEnabled(), this.loggerService.getLevel());
        Map<Subreport, Method> subreportMap = new LinkedHashMap<>();
        Function<Pair<Subreport, Method>, Void> subreportFunction = AnnotationUtils.getSubreportsFunction(subreportMap);
        AnnotationUtils.methodsWithSubreportAnnotations(container.getClazz(), subreportFunction, container.getReportData().getReportName());

        for (Map.Entry<Subreport, Method> entry : subreportMap.entrySet()) {
            final Subreport subreportAnnotation = entry.getKey();
            final Method method = entry.getValue();
            Class<?> componentType = method.getReturnType();
            method.setAccessible(true);

            if(ReflectionUtils.isListOrArray(componentType)){
                parseIterableSubreports(container, idPrefix, dataParser, method, subreportAnnotation, componentType);
            } else {
                parseSubreport(container, subreportAnnotation, dataParser, componentType, method, idPrefix);
            }
        }
    }

    private void parseIterableSubreports(final ListDataContainer<T> container, String idPrefix, DataParser<?> dataParser, Method method, Subreport subreportAnnotation, Class<?> returnType) throws GreportsReflectionException {
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

        parseIterableSubreports(container, subreportAnnotation, dataParser, componentType, idPrefix, subreportsList, subreportPositionalIncrement);
    }

    private void parseIterableSubreports(final ListDataContainer<T> container, Subreport subreportAnnotation, DataParser<?> dataParser, Class<?> returnType, String idPrefix, List<List<?>> subreportsList, float subreportPositionalIncrement) throws GreportsReflectionException {
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
                    dataParser,
                    returnType,
                        Utils.generateId(Utils.generateId(idPrefix, subreportAnnotation.id()), Integer.toString(i)), positionalIncrement + subreportAnnotation.position(), subreportData
                );
                positionalIncrement += subreportPositionalIncrement;
            }
        }
    }

    private void parseSubreport(final ListDataContainer<T> container, Subreport subreportAnnotation, final DataParser<?> dataParser, Class<?> returnType, Method method, String idPrefix) throws GreportsReflectionException {
        float subreportPositionalIncrement = AnnotationUtils.getSubreportLastColumn(returnType, container.getReportData().getReportName()).position() + SUBREPORT_POSITIONAL_INCREMENT;
        final List<Object> subreportData = new ArrayList<>();
        for (T entry : container.getData()) {
            final Object invokeResult = ReflectionUtils.invokeMethod(method, entry);
            subreportData.add(invokeResult);
        }
        final float increment = subreportPositionalIncrement + subreportAnnotation.position();
        final String generatedId = Utils.generateId(idPrefix, subreportAnnotation.id());
        parseSubreportData(container, dataParser, returnType, generatedId, increment, subreportData);
    }

    @SuppressWarnings("unchecked")
    private void parseSubreportData(final ListDataContainer<T> container, final DataParser dataParser, final Class<?> returnType, String idfPrefix, float positionalIncrement, final List subreportData) throws GreportsReflectionException {
        final Data reportData = container.getReportData();
        final Configurator configurator = container.getConfigurator();
        final DataParser<?> parse = dataParser
            .parse(subreportData, reportData.getReportName(), returnType, configurator.getReportGenerator().getConfigurator(returnType, reportData.getReportName()), positionalIncrement, idfPrefix);
        final Data data = parse.getContainer().getReportData();
        container.getSubreportsData().add(data);
    }

    private void parseSpecialColumns(final ListDataContainer<T> container) throws GreportsReflectionException {
        final List<T> list = container.getData();
        final Data data = container.getReportData();
        final Class<T> clazz = container.getClazz();
        for (SpecialColumn specialColumn : data.getConfiguration().getSpecialColumns()) {
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
                data.getDataRows().get(i).addCell(new DataCell(
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

    private void parseSpecialRows(final ListDataContainer<T> container) throws GreportsReflectionException {
        final Data data = container.getReportData();
        final Class<T> clazz = container.getClazz();

        List<SpecialRow> specialRows = data.getConfiguration().getSpecialRows();
        for (SpecialRow specialRow : specialRows) {
            SpecialDataRow specialDataRow = new SpecialDataRow(specialRow.rowIndex(), specialRow.stickyRow());

            SpecialRowCell[] cells = specialRow.cells();
            for (SpecialRowCell cell : cells) {
                SpecialDataRowCell specialDataRowCell = new SpecialDataRowCell(cell);

                if(!specialDataRowCell.getValueType().equals(ValueType.COLLECTED_VALUE) && !specialDataRowCell.getValueType().equals(ValueType.COLLECTED_FORMULA_VALUE)) {
                    specialDataRow.addCell(createSpecialDataCell(container, specialDataRowCell, specialDataRowCell.getValue()));
                } else if(specialDataRowCell.getValueType().equals(ValueType.COLLECTED_VALUE) && CollectedValues.class.isAssignableFrom(clazz)){
                    parseSpecialRowCollectedValue(container, specialDataRow, specialDataRowCell);
                } else if(specialDataRowCell.getValueType().equals(ValueType.COLLECTED_FORMULA_VALUE) && CollectedFormulaValues.class.isAssignableFrom(clazz)) {
                    parseSpecialRowCollectedFormulaValue(container, specialDataRow, specialDataRowCell);
                }
            }
            data.addSpecialRow(specialDataRow);
        }
    }

    private void parseSpecialRowCollectedFormulaValue(final ListDataContainer<T> container, SpecialDataRow specialDataRow, SpecialDataRowCell specialRowCell) {
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
        SpecialDataRowCell specialDataRowCell = createSpecialDataCell(container, specialRowCell, specialRowCell.getValue()).setValuesById(valuesById);
        specialDataRow.addCell(specialDataRowCell);
    }

    private void parseSpecialRowCollectedValue(final ListDataContainer<T> container, SpecialDataRow specialDataRow, SpecialDataRowCell specialRowCell) throws GreportsReflectionException {
        final Class<T> clazz = container.getClazz();
        final Data data = container.getReportData();
        final T newInstance = ReflectionUtils.newInstance(clazz);
        Pair<String, Object> pair = Pair.of(data.getReportName(), specialRowCell.getValue());
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

    private static <T> SpecialDataRowCell createSpecialDataCell(final ListDataContainer<T> container, SpecialDataRowCell specialRowCell, Object value) {
        return new SpecialDataRowCell(
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

    private void parseStyles(final ListDataContainer<T> container) throws GreportsReflectionException {
        final Data reportData = container.getReportData();
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

    private void parseConfitionalRowStyles(final ListDataContainer<T> container, int startRowIndex, ReportStylesBuilder reportStylesBuilder, int i, ConditionalRowStyles entry) {
        final Data data = container.getReportData();
        final Optional<Map<String, IntPredicate>> styledOptional = Optional.ofNullable(entry.isStyled());
        final List<ReportStyleBuilder<HorizontalRange>> horizontalRangedStyleBuilders = entry.getIndexBasedStyle().getOrDefault(data.getReportName(), new ArrayList<>());
        final IntPredicate predicate = styledOptional
                .orElseThrow(() -> new GreportsRuntimeException("The returned map cannot be null", this.getClass()))
                .getOrDefault(data.getReportName(), null);
        if(predicate != null && predicate.test(i)) {
            for(ReportStyleBuilder<HorizontalRange> styleBuilder : horizontalRangedStyleBuilders) {
                reportStylesBuilder.addStyleBuilder(new ReportStyleBuilder<>(new VerticalRange(startRowIndex + i, startRowIndex + i), styleBuilder.toRectangeRangeStyleBuilder()));
            }
        }
    }

    private void parseConditionalCellStyles(final ListDataContainer<T> container, int startRowIndex, ReportStylesBuilder reportStylesBuilder, int i, ConditionalCellStyles entry) {
        final Data data = container.getReportData();
        final Optional<Map<String, List<Pair<String, Predicate<Integer>>>>> styledOptional = Optional.ofNullable(entry.isCellStyled());
        final List<Pair<String, Predicate<Integer>>> predicatePairs = styledOptional
                .orElseThrow(() -> new GreportsRuntimeException("The returned map cannot be null", this.getClass()))
                .getOrDefault(data.getReportName(), null);
        final List<Pair<String, ReportStyleBuilder<Position>>> styleBuilders = entry.getIndexBasedCellStyle().getOrDefault(data.getReportName(), null);
        for(Pair<String, Predicate<Integer>> predicatePair : predicatePairs) {
            if(predicatePair.getRight() != null && predicatePair.getRight().test(i)) {
                for(Pair<String, ReportStyleBuilder<Position>> styleBuilderPair : styleBuilders) {
                    if(styleBuilderPair.getLeft().equals(predicatePair.getLeft())) {
                        final ReportStyleBuilder<Position> positionedStyleBuilder = styleBuilderPair.getRight();
                        final Position position = new Position(startRowIndex + i, data.getColumnIndexForId(styleBuilderPair.getLeft()));
                        reportStylesBuilder.addStyleBuilder(new ReportStyleBuilder<>(position, positionedStyleBuilder));
                    }
                }
            }
        }
    }

     ListDataContainer<T> getContainer() {
        return currentContainer;
    }
}