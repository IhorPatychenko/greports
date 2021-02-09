package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.greports.annotations.Cell;
import org.greports.content.cell.DataCell;
import org.greports.content.row.DataRow;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.exceptions.ReportEngineRuntimeException;
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

public class ReportSingleDataParser extends ReportParser {

    private LoggerService loggerService;

    private ReportData reportData;

    public ReportSingleDataParser(boolean loggerEnabled, Level level) {
        loggerService = LoggerService.forClass(ReportSingleDataParser.class, loggerEnabled, level);
    }

    public <T> ReportSingleDataParser parse(final T object, final String reportName, final Class<T> clazz) throws ReportEngineReflectionException {
        loggerService.info("Parsing started...");
        loggerService.info(String.format("Parsing report for class \"%s\" with name \"%s\"...", clazz.getSimpleName(), reportName));
        Stopwatch timer = Stopwatch.createStarted();
        ReportConfiguration configuration = ReportConfigurationLoader.load(clazz, reportName);
        reportData = new ReportData(reportName, configuration);
        parseData(object, clazz);
        super.parseStyles(reportData, clazz);
        loggerService.info(String.format("Report with name \"%s\" successfully parsed. Parse time: %s", reportName, timer.stop()));
        return this;
    }

    private <T> void parseData(final T object, final Class<T> clazz) throws ReportEngineReflectionException {
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
                Object cellValue = object != null ? method.invoke(object ) : null;
                if(cell.getterConverter().length > 1){
                    throw new ReportEngineRuntimeException("A cell cannot have more than 1 getter converter", clazz);
                } else if(cell.getterConverter().length == 1){
                    cellValue = ConverterUtils.convertValue(cellValue, cell.getterConverter()[0]);
                }
                DataCell dataCell = new DataCell(
                        (float) cell.column(),
                        true,
                        cell.format(),
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

    ReportData getData(){
        return reportData;
    }

}
