import annotations.Report;
import annotations.ReportColumn;
import annotations.ReportColumns;
import annotations.ReportSpecialCell;
import annotations.ReportSpecialRow;
import annotations.ReportTemplate;
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

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

final class ReportDataParser {

    private String reportLang;
    private ReportData reportData;
    private Map<String, Object> translations;
    private Collection<ReportDataColumn> emptyColumns;
    private Report reportAnnotation;

    ReportDataParser(String lang) {
        this.reportLang = lang;
    }

    <T> ReportDataParser parse(Collection<T> collection, final String reportName) throws Exception {
        T firstElement = collection.iterator().next();
        checkCollectionNotEmpty(collection);
        reportAnnotation = AnnotationUtils.getReportAnnotation(reportName, firstElement.getClass());
        reportData = new ReportData(reportName, reportAnnotation.sheetName());
        translations = new TranslationsParser(reportAnnotation.translationsDir()).parse(this.reportLang);
        AnnotationUtils.checkReportAnnotation(reportAnnotation, firstElement.getClass(), reportData.getName());
        loadReportTemplate();
        loadReportHeader(firstElement);
        loadReportRows(collection);
        loadReportSpecialRows();
        loadReportStyles(firstElement);
        return this;
    }

    private void loadReportTemplate() throws Exception {
        final ReportTemplate reportTemplate = Arrays.stream(reportAnnotation.templates())
                .filter(template -> template.reportName().equals(reportData.getName()))
                .findFirst()
                .orElse(null);
        if(!Objects.isNull(reportTemplate)){
            try {
                InputStream fileStream = new FileInputStream(reportTemplate.templatePath());
                reportData.setTemplateInputStream(fileStream);
            } catch (FileNotFoundException e) {
                throw new Exception("No template found with path \"" + reportTemplate.templatePath() + "\"");
            }
        }
    }

    private <T> void loadReportHeader(T dto) {
        reportData.setShowHeader(reportAnnotation.showHeader());
        if(reportData.isShowHeader()){
            reportData.setHeaderStartRow(reportAnnotation.headerOffset());
            loadEmptyColumns();
            List<ReportHeaderCell> cells = new ArrayList<>();
            Function<AbstractMap.SimpleEntry<Method, ReportColumn>, Void> columnFunction = list -> {
                ReportColumn column = list.getValue();
                cells.add(new ReportHeaderCell(column.position(), (String) translations.getOrDefault(column.title(), column.title())));
                return null;
            };
            AnnotationUtils.loadMethodsColumns(dto.getClass(), columnFunction, reportData.getName());
            cells.addAll(ReportHeaderCell.from(emptyColumns));
            cells.sort(Comparator.comparing(ReportCell::getPosition));
            reportData.setHeader(new ReportHeader(reportAnnotation.sortableHeader()))
                    .addCells(cells);
        }
    }

    private <T> void loadReportRows(Collection<T> collection) throws Exception {
        loadEmptyColumns();
        loadRowsData(collection);
    }

    private <T> void loadRowsData(Collection<T> collection) throws Exception {
        reportData.setDataStartRow(reportAnnotation.dataOffset());

        Map<Method, ReportColumn> methodsMap = new LinkedHashMap<>();
        Map<Method, ReportColumn> finalMethodsMap = methodsMap;
        Function<AbstractMap.SimpleEntry<Method, ReportColumn>, Void> columnFunction = list -> {
            Method method = list.getKey();
            ReportColumn column = list.getValue();
            finalMethodsMap.put(method, column);
            return null;
        };

        AnnotationUtils.loadMethodsColumns(collection.iterator().next().getClass(), columnFunction, reportData.getName());

        methodsMap = sortMethodsMapByColumnOrder(finalMethodsMap);
        reportData.setColumnsLength(methodsMap.size());

        for (T dto : collection) {
            ReportDataRow row = new ReportDataRow();
            for(Map.Entry<Method, ReportColumn> entry : methodsMap.entrySet()){
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
        for(ReportSpecialRow reportSpecialRow : reportAnnotation.specialRows()){
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

    private Map<Method, ReportColumn> sortMethodsMapByColumnOrder(Map<Method, ReportColumn> methodsMap) {
        List<Map.Entry<Method, ReportColumn>> list = new ArrayList<>(methodsMap.entrySet());
        return list
                .stream()
                .sorted(Comparator.comparing(o -> o.getValue().position()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
    }

    private void loadEmptyColumns() {
        emptyColumns = Arrays.stream(reportAnnotation.emptyColumns())
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

    protected ReportData getData(){
        return reportData;
    }

    public ReportDataParser clear(){
        reportData = null;
        emptyColumns = null;
        reportAnnotation = null;
        return this;
    }

}
