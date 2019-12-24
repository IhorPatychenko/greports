package engine;

import annotations.Report;
import annotations.GeneratorColumn;
import annotations.Configuration;
import annotations.SpecialRowCell;
import annotations.SpecialRow;
import annotations.GeneratorSubreport;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

final class ReportDataParser {

    private String reportLang;
    private ReportData reportData;
    private Map<String, Object> translations;
    private Configuration configuration;

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
        loadReportSpecialRows();
        loadReportStyles(clazz);
        loadSubreports(collection, clazz);
        return this;
    }

    private <T> void loadReportHeader(Class<T> clazz) {
        reportData.setShowHeader(configuration.showHeader());
        reportData.setHeaderStartRow(configuration.headerOffset());
        if(reportData.isShowHeader()){
            List<ReportHeaderCell> cells = new ArrayList<>();
            Function<AbstractMap.SimpleEntry<Method, GeneratorColumn>, Void> columnFunction = list -> {
                GeneratorColumn column = list.getValue();
                cells.add(new ReportHeaderCell(column.position(), (String) translations.getOrDefault(column.title(), column.title()), column.id(), column.autoSizeColumn()));
                return null;
            };
            AnnotationUtils.generatorMethodsWithColumnAnnotations(clazz, columnFunction, reportData.getName());
            loadAutosizeColumns(cells);
            reportData.setHeader(new ReportHeader(configuration.sortableHeader()))
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
        reportData.setDataStartRow(configuration.dataOffset());

        Map<Method, GeneratorColumn> methodsMap = new LinkedHashMap<>();
        Function<AbstractMap.SimpleEntry<Method, GeneratorColumn>, Void> columnFunction = list -> {
            methodsMap.put(list.getKey(), list.getValue());
            return null;
        };

        AnnotationUtils.generatorMethodsWithColumnAnnotations(clazz, columnFunction, reportData.getName());

        for (T dto : collection) {
            ReportDataRow row = new ReportDataRow();
            for(Map.Entry<Method, GeneratorColumn> entry : methodsMap.entrySet()){
                try {
                    entry.getKey().setAccessible(true);
                    final Object invokedValue = dto != null ? entry.getKey().invoke(dto) : null;
                    ReportDataCell reportDataCell = new ReportDataCell(
                        entry.getValue().position(),
                        entry.getValue().format(),
                        invokedValue,
                        Arrays.asList(entry.getValue().targetIds()),
                        entry.getValue().valueType()
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
        Map<Method, GeneratorSubreport> methodsMap = new LinkedHashMap<>();
        Function<AbstractMap.SimpleEntry<Method, GeneratorSubreport>, Void> subreportFunction = list -> {
            methodsMap.put(list.getKey(), list.getValue());
            return null;
        };
        AnnotationUtils.generatorMethodWithSubreportAnnotations(clazz, subreportFunction, reportData.getName());
        List<ReportData> subreportsData = new ArrayList<>();
        for (Map.Entry<Method, GeneratorSubreport> entry : methodsMap.entrySet()) {
            final Method method = entry.getKey();
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
        reportData.mergeReportData(subreportsData);
    }

    private void loadReportSpecialRows(){
        for(SpecialRow specialRow : configuration.specialRows()){
            final ReportDataSpecialRow reportDataSpecialRow = new ReportDataSpecialRow(specialRow.rowIndex());
            for (final SpecialRowCell column : specialRow.cells()) {
                reportDataSpecialRow.addCell(new ReportDataSpecialRowCell(column.valueType(), column.value(), column.format(), column.targetId()));
            }
            reportData.addSpecialRow(reportDataSpecialRow);
        }
    }

    private <T> void loadReportStyles(Class<T> clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
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