package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.greports.annotations.Cell;
import org.greports.content.cell.DataCell;
import org.greports.content.row.DataRow;
import org.greports.converters.NotImplementedConverter;
import org.greports.exceptions.GreportsReflectionException;
import org.greports.services.LoggerService;
import org.greports.utils.AnnotationUtils;
import org.greports.utils.ConverterUtils;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class SingleDataParser<T> extends Parser {

    private final LoggerService loggerService;

    private SingleDataContainer<T> currentContainer;

    public SingleDataParser(boolean loggerEnabled, Level level) {
        loggerService = LoggerService.forClass(SingleDataParser.class, loggerEnabled, level);
    }

    public SingleDataParser<T> parse(final T object, final String reportName, final Class<T> clazz, Configurator configurator) throws GreportsReflectionException {
        loggerService.info("Parsing started...");
        loggerService.info(String.format("Parsing report for class \"%s\" with name \"%s\"...", clazz.getSimpleName(), reportName));
        Stopwatch timer = Stopwatch.createStarted();
        Configuration configuration = Configuration.load(clazz, reportName);
        SingleDataContainer<T> container = new SingleDataContainer<>(new Data(reportName, configuration), clazz);

        currentContainer = container;

        container.setObject(object)
                .setConfigurator(configurator);
        parseData(container);
        super.parseStyles(container);
        container.getReportData().applyConfigurator(configurator);
        loggerService.info(String.format("Report with name \"%s\" successfully parsed. Parse time: %s", reportName, timer.stop()));
        return this;
    }

    private void parseData(SingleDataContainer<T> container) throws GreportsReflectionException {
        final Data data = container.getReportData();
        final Class<T> clazz = container.getClazz();
        final T object = container.getObject();
        final Configurator configurator = container.getConfigurator();
        data.setDataStartRow(data.getConfiguration().getDataStartRowIndex());
        Map<Integer, DataRow> rows = new HashMap<>();

        Map<Cell, Method> cellMap = new LinkedHashMap<>();
        Function<Pair<Cell, Method>, Void> cellFunction = AnnotationUtils.getCellsAndMethodsFunction(cellMap);
        AnnotationUtils.cellsWithMethodsFunction(clazz, cellFunction, data.getReportName());
        for (final Map.Entry<Cell, Method> entry : cellMap.entrySet()) {
            final Cell cell = entry.getKey();
            final Method method = entry.getValue();
            Integer rowIndex = data.getConfiguration().getDataStartRowIndex() + cell.row();
            rows.putIfAbsent(rowIndex, new DataRow(rowIndex));
            final DataRow dataRow = rows.get(rowIndex);
            method.setAccessible(true);
            Object cellValue = super.checkNestedValue(object, method, AnnotationUtils.hasNestedTarget(cell), cell.target());

            if(!cell.getterConverter().converterClass().equals(NotImplementedConverter.class)){
                cellValue = ConverterUtils.convertValue(cellValue, cell.getterConverter());
            }

            String format = cell.format();

            if(cellValue != null) {
                format = configurator.getFormatForClass(cellValue.getClass(), format);
                if(cell.translate() && cellValue instanceof String) {
                    cellValue = container.getTranslator().translate(Objects.toString(cellValue));
                }
            }

            DataCell dataCell = new DataCell(
                    (float) cell.column(),
                    true,
                    format,
                    cellValue,
                    cell.valueType(),
                    cell.columnWidth()
            );
            dataRow.addCell(dataCell);
        }
        for (final Map.Entry<Integer, DataRow> dataRowEntry : rows.entrySet()) {
            data.addRow(dataRowEntry.getValue());
        }
    }

    SingleDataContainer<T> getContainer(){
        return currentContainer;
    }

}
