package engine;

import annotations.Report;
import annotations.ReportGeneratorColumn;
import annotations.ReportConfiguration;
import annotations.ReportSpecialCell;
import annotations.ReportSpecialRow;
import content.cell.ReportCell;
import content.cell.ReportDataColumn;
import content.cell.ReportDataSpecialCell;
import content.cell.ReportHeaderCell;
import content.ReportData;
import content.row.ReportDataRow;
import content.ReportHeader;
import content.row.ReportDataSpecialRow;
import styles.interfaces.StripedRows;
import styles.interfaces.StyledReport;
import positioning.TranslationsParser;
import utils.AnnotationUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

final class ReportDataParser {

    private String reportLang;
    private ReportData reportData;
    private Map<String, Object> translations;
    private Collection<ReportDataColumn> emptyColumns;
    private ReportConfiguration reportConfiguration;

    ReportDataParser(String lang) {
        this.reportLang = lang;
    }

    <T> ReportDataParser parse(Collection<T> collection, final String reportName) throws Exception {
        T firstElement = collection.iterator().next();
        checkCollectionNotEmpty(collection);
        final Report reportAnnotation = AnnotationUtils.getReportAnnotation(firstElement.getClass());
        reportConfiguration = AnnotationUtils.getReportConfiguration(reportAnnotation, firstElement.getClass(), reportName);
        reportData = new ReportData(reportName, reportConfiguration.sheetName());
        translations = new TranslationsParser(reportAnnotation.translationsDir()).parse(reportLang);
        loadReportHeader(firstElement);
        loadRowsData(collection);
        loadReportSpecialRows();
        loadReportStyles(firstElement);
        return this;
    }

    private <T> void loadReportHeader(T dto) {
        reportData.setShowHeader(reportConfiguration.showHeader());
        reportData.setHeaderStartRow(reportConfiguration.headerOffset());
        loadEmptyColumns();
        if(reportData.isShowHeader()){
            List<ReportHeaderCell> cells = new ArrayList<>();
            Function<AbstractMap.SimpleEntry<Method, ReportGeneratorColumn>, Void> columnFunction = list -> {
                ReportGeneratorColumn column = list.getValue();
                cells.add(new ReportHeaderCell(column.position(), (String) translations.getOrDefault(column.title(), column.title()), column.autoSizeColumn()));
                return null;
            };
            AnnotationUtils.reportGeneratorMethodsWithColumnAnnotations(dto.getClass(), columnFunction, AnnotationUtils.getReportColumnsPredicate(reportData.getName()));
            cells.addAll(ReportHeaderCell.fromEmptyColumns(emptyColumns));
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

    private <T> void loadRowsData(Collection<T> collection) throws Exception {
        reportData.setDataStartRow(reportConfiguration.dataOffset());

        Map<Method, ReportGeneratorColumn> methodsMap = new LinkedHashMap<>();
        Map<Method, ReportGeneratorColumn> finalMethodsMap = methodsMap;
        Function<AbstractMap.SimpleEntry<Method, ReportGeneratorColumn>, Void> columnFunction = list -> {
            Method method = list.getKey();
            ReportGeneratorColumn column = list.getValue();
            finalMethodsMap.put(method, column);
            return null;
        };

        AnnotationUtils.reportGeneratorMethodsWithColumnAnnotations(collection.iterator().next().getClass(), columnFunction, AnnotationUtils.getReportColumnsPredicate(reportData.getName()));

        methodsMap = sortMethodsMapByColumnOrder(finalMethodsMap);
        reportData.setColumnsCount(methodsMap.size());

        for (T dto : collection) {
            ReportDataRow row = new ReportDataRow();
            for(Map.Entry<Method, ReportGeneratorColumn> entry : methodsMap.entrySet()){
                try {
                    final Object invokedValue = entry.getKey().invoke(dto);
                    ReportDataColumn reportDataColumn = new ReportDataColumn(
                            entry.getValue().position(),
                            entry.getValue().format(),
                            invokedValue,
                            entry.getValue().id()
                    );
                    row.addColumn(reportDataColumn);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new Exception("Error obtaining the value of column");
                }

            }
            emptyColumns.forEach(row::addColumn);
            row.getColumns().sort(Comparator.comparing(ReportCell::getPosition));
            reportData.addRow(row);
        }
    }

    private void loadReportSpecialRows(){
        for(ReportSpecialRow reportSpecialRow : reportConfiguration.specialRows()){
            final ReportDataSpecialRow reportDataSpecialRow = new ReportDataSpecialRow(reportSpecialRow.rowIndex());
            for (final ReportSpecialCell column : reportSpecialRow.columns()) {
                final ReportDataSpecialCell reportDataSpecialCell = new ReportDataSpecialCell(column.targetId(), column.valueType(), column.value());
                getSpecialCellColumnIndex(reportDataSpecialCell);
                reportDataSpecialRow.addCell(reportDataSpecialCell);
            }
            reportData.addSpecialRow(reportDataSpecialRow);
        }
    }

    private void getSpecialCellColumnIndex(final ReportDataSpecialCell reportDataSpecialCell) {
        final ReportDataRow firstRow = reportData.getRow(0);
        for (int i = 0; i < firstRow.getColumns().size(); i++) {
            final ReportDataColumn column = firstRow.getColumn(i);
            if(reportDataSpecialCell.getTargetId().equalsIgnoreCase(column.getId())){
                reportDataSpecialCell.setColumnIndex(i);
                break;
            }
        }
    }

    private <T> void loadReportStyles(T firstElement) {
        if(firstElement instanceof StyledReport){
            StyledReport elem = (StyledReport) firstElement;
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
        if(firstElement instanceof StripedRows){
            StripedRows elem = (StripedRows) firstElement;
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

    private void loadEmptyColumns() {
        emptyColumns = Arrays.stream(reportConfiguration.emptyColumns())
                .filter(column -> reportData.getName().equals(column.reportName()))
                .map(column -> new ReportDataColumn(
                        column.position(),
                        (String) translations.getOrDefault(column.title(), column.title()),
                        null,
                        null,
                        null
                )).collect(Collectors.toList());
    }

    private <T> void checkCollectionNotEmpty(Collection<T> collection) throws Exception {
        if (collection.isEmpty()) {
            throw new Exception("The collection cannot be empty");
        }
    }

    ReportData getData(){
        return reportData;
    }

}
