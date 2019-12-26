package engine;

import annotations.Column;
import annotations.Report;
import annotations.Configuration;
import annotations.SpecialColumn;
import annotations.SpecialRowCell;
import annotations.SpecialRow;
import annotations.Subreport;
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
import utils.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

final class ReportDataParser {

    private String reportLang;
    private ReportData reportData;
    private Map<String, Object> translations;
    private Configuration configuration;
    private List<ReportData> subreportsData = new ArrayList<>();

    ReportDataParser(String lang) {
        this.reportLang = lang;
    }

    <T> ReportDataParser parse(T item, final String reportName, Class<T> clazz) throws Exception {
        return parse(Collections.singletonList(item), reportName, clazz);
    }

    <T> ReportDataParser parse(Collection<T> collection, final String reportName, Class<T> clazz) throws Exception {
        final Report reportAnnotation = AnnotationUtils.getReportAnnotation(clazz);
        configuration = AnnotationUtils.getReportConfiguration(reportAnnotation, reportName);
        reportData = new ReportData(reportName, configuration.sheetName());
        translations = new TranslationsParser(reportAnnotation.translationsDir()).parse(reportLang);
        loadReportHeader(clazz);
        loadRowsData(collection, clazz);
        loadSpecialColumns();
        loadSpecialRows();
        loadStyles(clazz);
        loadSubreports(collection, clazz);
        reportData.mergeReportData(subreportsData);
        return this;
    }

    private <T> void loadReportHeader(Class<T> clazz) {
        reportData.setShowHeader(configuration.showHeader());
        reportData.setHeaderStartRow(configuration.headerOffset());
        List<ReportHeaderCell> cells = new ArrayList<>();
        Function<Pair<Field, Column>, Void> columnFunction = AnnotationUtils.getHeadersFunction(cells, translations);
        AnnotationUtils.fieldsWithColumnAnnotations(clazz, columnFunction, reportData.getName());

        for (SpecialColumn specialColumn : configuration.specialColumns()) {
            cells.add(new ReportHeaderCell(specialColumn.position(), specialColumn.title(), "", specialColumn.autoSizeColumn()));
        }
        loadAutosizeColumns(cells);
        reportData.setHeader(new ReportHeader(configuration.sortableHeader()))
                .addCells(cells);
    }

    private void loadAutosizeColumns(List<ReportHeaderCell> cells){
        for (int i = 0; i < cells.size(); i++) {
            if(cells.get(i).isAutoSizeColumn()){
                reportData.getAutoSizedColumns().add(i);
            }
        }
    }

    private <T> void loadRowsData(Collection<T> collection, Class<T> clazz) throws Exception {
        reportData.setDataStartRow(configuration.dataOffset());

        Map<Field, Column> columnsMap = new LinkedHashMap<>();
        Map<Field, Method> methodsMap = new LinkedHashMap<>();
        Function<Pair<Field, Column>, Void> columnFunction = AnnotationUtils.getFieldsAndColumnsFunction(columnsMap);

        AnnotationUtils.fieldsWithColumnAnnotations(clazz, columnFunction, reportData.getName());

        for (Map.Entry<Field, Column> entry : columnsMap.entrySet()) {
            methodsMap.put(entry.getKey(), AnnotationUtils.fetchFieldGetter(entry.getKey(), clazz));
        }

        for (T dto : collection) {
            ReportDataRow row = new ReportDataRow();
            for(Map.Entry<Field, Method> entry : methodsMap.entrySet()){
                try {
                    final Field field = entry.getKey();
                    final Method method = entry.getValue();
                    final Column column = columnsMap.get(field);
                    method.setAccessible(true);
                    final Object invokedValue = dto != null ? method.invoke(dto) : null;
                    ReportDataCell reportDataCell = new ReportDataCell(
                        column.position(),
                        column.format(),
                        invokedValue,
                        Arrays.asList(column.targetIds()),
                        column.valueType(),
                        false
                    );
                    row.addCell(reportDataCell);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new Exception("Error obtaining the value of column");
                }
            }
            reportData.addRow(row);
        }
    }

    private <T> void loadSubreports(Collection<T> collection, Class<T> clazz) throws Exception {
        Map<Field, Subreport> subreportMap = new LinkedHashMap<>();
        Map<Field, Method> methodsMap = new LinkedHashMap<>();
        Function<Pair<Field, Subreport>, Void> subreportFunction = AnnotationUtils.getSubreportsFunction(subreportMap);
        AnnotationUtils.fieldsWithSubreportAnnotations(clazz, subreportFunction, reportData.getName());

        for (Map.Entry<Field, Subreport> entry : subreportMap.entrySet()) {
            methodsMap.put(entry.getKey(), AnnotationUtils.fetchFieldGetter(entry.getKey(), clazz));
        }

        for (Map.Entry<Field, Subreport> entry : subreportMap.entrySet()) {
            final Field field = entry.getKey();
            final Method method = methodsMap.get(field);
            final Class<?> returnType = method.getReturnType();
            final Collection subreportData = new ArrayList<>();
            for (T collectionEntry : collection) {
                method.setAccessible(true);
                try {
                    final Object invoke = method.invoke(collectionEntry);
                    subreportData.add(invoke);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new Exception("Error obtaining the value of column");
                }
            }
            final ReportDataParser reportDataParser = new ReportDataParser(reportLang);
            final ReportData data = reportDataParser.parse(subreportData, reportData.getName(), returnType).getData();
            subreportsData.add(data);
        }
    }

    private void loadSpecialColumns(){
        for (SpecialColumn specialColumn : configuration.specialColumns()) {
            for (ReportDataRow row : reportData.getRows()) {
                row.addCell(new ReportDataCell(
                    specialColumn.position(),
                    specialColumn.format(),
                    specialColumn.value(),
                    Arrays.asList(specialColumn.targetIds()),
                    specialColumn.valueType(),
                    specialColumn.isRangedFormula()));
            }
        }
    }

    private void loadSpecialRows(){
        for(SpecialRow specialRow : configuration.specialRows()){
            final ReportDataSpecialRow reportDataSpecialRow = new ReportDataSpecialRow(specialRow.rowIndex());
            for (final SpecialRowCell column : specialRow.cells()) {
                reportDataSpecialRow.addCell(new ReportDataSpecialRowCell(column.valueType(), column.value(), column.format(), column.targetId()));
            }
            reportData.addSpecialRow(reportDataSpecialRow);
        }
    }

    private <T> void loadStyles(Class<T> clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        final Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        final T newInstance = constructor.newInstance();
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

    ReportData getData(){
        return reportData;
    }

}