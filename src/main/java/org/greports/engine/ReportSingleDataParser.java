package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.greports.annotations.Cell;
import org.greports.content.cell.DataCell;
import org.greports.content.row.DataRow;
import org.greports.converters.NotImplementedConverter;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.services.LoggerService;
import org.greports.utils.AnnotationUtils;
import org.greports.utils.ConverterUtils;
import org.greports.utils.ErrorMessages;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class ReportSingleDataParser<T> extends ReportParser {

    private final LoggerService loggerService;

    private ReportSingleDataContainer<T> currentContainer;

    public ReportSingleDataParser(boolean loggerEnabled, Level level) {
        loggerService = LoggerService.forClass(ReportSingleDataParser.class, loggerEnabled, level);
    }

    public ReportSingleDataParser<T> parse(final T object, final String reportName, final Class<T> clazz, ReportConfigurator configurator) throws ReportEngineReflectionException {
        loggerService.info("Parsing started...");
        loggerService.info(String.format("Parsing report for class \"%s\" with name \"%s\"...", clazz.getSimpleName(), reportName));
        Stopwatch timer = Stopwatch.createStarted();
        ReportConfiguration configuration = ReportConfigurationLoader.load(clazz, reportName);
        ReportSingleDataContainer<T> container = new ReportSingleDataContainer<>(new ReportData(reportName, configuration), clazz);

        currentContainer = container;

        container.setObject(object)
                .setConfigurator(configurator);
        parseData(container);
        super.parseStyles(container);
        container.getReportData().applyConfigurator(configurator);
        loggerService.info(String.format("Report with name \"%s\" successfully parsed. Parse time: %s", reportName, timer.stop()));
        return this;
    }

    private void parseData(ReportSingleDataContainer<T> container) throws ReportEngineReflectionException {
        final ReportData reportData = container.getReportData();
        final Class<T> clazz = container.getClazz();
        final T object = container.getObject();
        final ReportConfigurator configurator = container.getConfigurator();
        reportData.setDataStartRow(reportData.getConfiguration().getDataStartRowIndex());
        Map<Integer, DataRow> rows = new HashMap<>();

        Map<Cell, Method> cellMap = new LinkedHashMap<>();
        Function<Pair<Cell, Method>, Void> cellFunction = AnnotationUtils.getCellsAndMethodsFunction(cellMap);
        AnnotationUtils.cellsWithMethodsFunction(clazz, cellFunction, reportData.getReportName());
        for (final Map.Entry<Cell, Method> entry : cellMap.entrySet()) {
            final Cell cell = entry.getKey();
            final Method method = entry.getValue();
            Integer rowIndex = reportData.getConfiguration().getDataStartRowIndex() + cell.row();
            if(!rows.containsKey(rowIndex)){
                rows.put(rowIndex, new DataRow(rowIndex));
            }
            final DataRow dataRow = rows.get(rowIndex);
            method.setAccessible(true);
            try {
                Object cellValue = object != null ? method.invoke(object) : null;
                if(!cell.getterConverter().converterClass().equals(NotImplementedConverter.class)){
                    cellValue = ConverterUtils.convertValue(cellValue, cell.getterConverter());
                }

                String format = cell.format();

                if(cellValue != null) {
                    format = configurator.getFormatForClass(cellValue.getClass(), format);
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
            } catch (IllegalAccessException e) {
                throw new ReportEngineReflectionException(ErrorMessages.INV_METHOD_WITH_NO_ACCESS, e, clazz);
            } catch (InvocationTargetException e) {
                throw new ReportEngineReflectionException(ErrorMessages.INV_METHOD, e, clazz);
            }
        }
        for (final Map.Entry<Integer, DataRow> dataRowEntry : rows.entrySet()) {
            reportData.addRow(dataRowEntry.getValue());
        }
    }

    ReportSingleDataContainer<T> getContainer(){
        return currentContainer;
    }

}
