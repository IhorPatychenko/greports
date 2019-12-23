package engine;

import annotations.Report;
import annotations.ReportGeneratorColumn;
import annotations.ReportConfiguration;
import annotations.ReportSpecialRowCell;
import annotations.ReportSpecialRow;
import content.cell.ReportCell;
import content.column.ReportDataCell;
import content.cell.ReportDataSpecialRowCell;
import content.cell.ReportHeaderCell;
import content.ReportData;
import content.row.ReportDataRow;
import content.ReportHeader;
import content.row.ReportDataSpecialRow;
import styles.interfaces.StripedRows;
import styles.interfaces.StyledReport;
import positioning.TranslationsParser;
import utils.AnnotationUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

final class ReportDataParser {

    private String reportLang;
    private ReportData reportData;
    private Map<String, Object> translations;
    private ReportConfiguration reportConfiguration;

    ReportDataParser(String lang) {
        this.reportLang = lang;
    }

    <T> ReportDataParser parse(T item, final String reportName, Class<T> clazz) throws Exception {
        return parse(Collections.singletonList(item), reportName, clazz);
    }

    <T> ReportDataParser parse(Collection<T> collection, final String reportName, Class<T> clazz) throws Exception {
        final Report reportAnnotation = AnnotationUtils.getReportAnnotation(clazz);
        reportConfiguration = AnnotationUtils.getReportConfiguration(reportAnnotation, reportName);
        reportData = new ReportData(reportName, reportConfiguration.sheetName());
        translations = new TranslationsParser(reportAnnotation.translationsDir()).parse(reportLang);
        loadReportHeader(clazz);
        loadRowsData(collection, clazz);
        loadReportSpecialRows();
        loadReportStyles(clazz);
        return this;
    }

    private <T> void loadReportHeader(Class<T> clazz) {
        reportData.setShowHeader(reportConfiguration.showHeader());
        reportData.setHeaderStartRow(reportConfiguration.headerOffset());
        if(reportData.isShowHeader()){
            List<ReportHeaderCell> cells = new ArrayList<>();
            Function<AbstractMap.SimpleEntry<Method, ReportGeneratorColumn>, Void> columnFunction = list -> {
                ReportGeneratorColumn column = list.getValue();
                cells.add(new ReportHeaderCell(column.position(), (String) translations.getOrDefault(column.title(), column.title()), column.autoSizeColumn()));
                return null;
            };
            AnnotationUtils.reportGeneratorMethodsWithColumnAnnotations(clazz, columnFunction, reportData.getName());
            cells.sort(Comparator.comparing(ReportCell::getPosition));
            loadAutosizeColumns(cells);
            reportData.setHeader(new ReportHeader(reportConfiguration.sortableHeader()))
                    .addCells(cells);
        }
    }

    private void loadAutosizeColumns(List<ReportHeaderCell> cells){
        for (int i = 0; i < cells.size(); i++) {
            if(cells.get(i).isAutoSizeColumn()){
                reportData.getAutoSizedColumns().add(i);
            }
        }
    }

    private <T> void loadRowsData(Collection<T> collection, Class<T> clazz) throws Exception {
        reportData.setDataStartRow(reportConfiguration.dataOffset());

        Map<Method, ReportGeneratorColumn> methodsMap = new LinkedHashMap<>();
        Map<Method, ReportGeneratorColumn> finalMethodsMap = methodsMap;
        Function<AbstractMap.SimpleEntry<Method, ReportGeneratorColumn>, Void> columnFunction = list -> {
            Method method = list.getKey();
            ReportGeneratorColumn column = list.getValue();
            finalMethodsMap.put(method, column);
            return null;
        };

        AnnotationUtils.reportGeneratorMethodsWithColumnAnnotations(clazz, columnFunction, reportData.getName());

        methodsMap = sortMethodsMapByColumnOrder(finalMethodsMap);
        reportData.setColumnsCount(methodsMap.size());

        final List<ReportGeneratorColumn> columns = new ArrayList<>(methodsMap.values());
        final Map<String, Integer> targetIndexes = new HashMap<>();
        for (int i = 0; i < columns.size(); i++) {
            targetIndexes.put(columns.get(i).id(), i);
        }
        reportData.setTargetsIndexes(targetIndexes);

        for (T dto : collection) {
            ReportDataRow row = new ReportDataRow();
            for(Map.Entry<Method, ReportGeneratorColumn> entry : methodsMap.entrySet()){
                try {
                    final Object invokedValue = entry.getKey().invoke(dto);
                    ReportDataCell reportDataCell = new ReportDataCell(
                            entry.getValue().position(),
                            entry.getValue().format(),
                            invokedValue,
                            entry.getValue().id(),
                            Arrays.asList(entry.getValue().targetIds()),
                            entry.getValue().valueType()
                    );
                    row.addCell(reportDataCell);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new Exception("Error obtaining the value of column");
                }

            }
            row.getColumns().sort(Comparator.comparing(ReportCell::getPosition));
            reportData.addRow(row);
        }
    }

    private void loadReportSpecialRows(){
        for(ReportSpecialRow reportSpecialRow : reportConfiguration.specialRows()){
            final ReportDataSpecialRow reportDataSpecialRow = new ReportDataSpecialRow(reportSpecialRow.rowIndex());
            for (final ReportSpecialRowCell column : reportSpecialRow.cells()) {
                reportDataSpecialRow.addCell(new ReportDataSpecialRowCell(column.valueType(), column.value(), column.format(), column.targetId()));
            }
            reportData.addSpecialRow(reportDataSpecialRow);
        }
    }

    private <T> void loadReportStyles(Class<T> clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException {
        final Constructor<T> constructor = clazz.getConstructor();
        constructor.setAccessible(true);
        final T newInstance = clazz.newInstance();
        final List<Class<?>> interfaces = Arrays.asList(clazz.getInterfaces());
        if(interfaces.contains(StyledReport.class)){
            StyledReport elem = (StyledReport) newInstance;
            if(elem.getRangedRowStyles() != null){
                reportData.getStyles().setRowStyles(elem.getRangedRowStyles().get(reportData.getName()));
            }
            if(elem.getRangedColumnStyles() != null){
                reportData.getStyles().setColumnStyles(elem.getRangedColumnStyles().get(reportData.getName()));
            }
            if(elem.getPositionedStyles() != null){
                reportData.getStyles().setPositionedStyles(elem.getPositionedStyles().get(reportData.getName()));
            }
            if(elem.getRectangleRangedStyles() != null){
                reportData.getStyles().setRangedStyleReportStyles(elem.getRectangleRangedStyles().get(reportData.getName()));
            }
        }
        if(interfaces.contains(StripedRows.class)){
            StripedRows elem = (StripedRows) newInstance;
            if(elem.getStripedRowsIndex() != null && elem.getStripedRowsColor() != null){
                reportData.getStyles()
                        .setStripedRowsIndex(elem.getStripedRowsIndex().getOrDefault(reportData.getName(), null))
                        .setStripedRowsColor(elem.getStripedRowsColor().getOrDefault(reportData.getName(), null));
            }
        }
    }

    private Map<Method, ReportGeneratorColumn> sortMethodsMapByColumnOrder(Map<Method, ReportGeneratorColumn> methodsMap) {
        List<Map.Entry<Method, ReportGeneratorColumn>> list = new ArrayList<>(methodsMap.entrySet());
        return list
                .stream()
                .sorted(Comparator.comparing(o -> o.getValue().position()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
    }

    ReportData getData(){
        return reportData;
    }

}
