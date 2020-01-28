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
import exceptions.ReportEngineReflectionException;
import styles.interfaces.StripedRows;
import styles.interfaces.StyledReport;
import positioning.TranslationsParser;
import utils.AnnotationUtils;
import utils.Pair;
import utils.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static exceptions.ReportEngineRuntimeExceptionCode.ILLEGAL_ACCESS;
import static exceptions.ReportEngineRuntimeExceptionCode.INSTANTIATION_ERROR;
import static exceptions.ReportEngineRuntimeExceptionCode.INVOCATION_ERROR;
import static exceptions.ReportEngineRuntimeExceptionCode.NO_METHOD_ERROR;

final class ReportDataParser {

    private ReportData reportData;
    private Map<String, Object> translations;
    private Configuration configuration;
    private final List<ReportData> subreportsData = new ArrayList<>();
    private static final float SUBREPORT_POSITIONAL_INCREMENT = 0.00000000000001f;

    public <T> ReportDataParser parse(Collection<T> collection, final String reportName, Class<T> clazz, ReportConfigurator configurator) throws ReportEngineReflectionException, IOException {
        final ReportDataParser parser = parse(collection, reportName, clazz, 0f);
        overrideSubreportsTitles(configurator.getOverriddenTitles());
        return parser;
    }

    private <T> ReportDataParser parse(Collection<T> collection, final String reportName, Class<T> clazz, Float positionIncrement) throws ReportEngineReflectionException, IOException {
        final Report reportAnnotation = AnnotationUtils.getReportAnnotation(clazz);
        configuration = AnnotationUtils.getReportConfiguration(reportAnnotation, reportName);
        reportData = new ReportData(reportName, configuration.sheetName(), !configuration.templatePath().equals("") ? getClass().getClassLoader().getResource(configuration.templatePath()) : null);
        translations = new TranslationsParser(configuration.translationsDir()).parse(configuration.reportLang());
        loadReportHeader(clazz, positionIncrement);
        loadRowsData(collection, clazz, positionIncrement);
        loadSpecialColumns(collection, clazz);
        loadSpecialRows();
        loadStyles(clazz);
        loadSubreports(collection, clazz);
        reportData.mergeReportData(subreportsData);
        return this;
    }

    private <T> void loadReportHeader(Class<T> clazz, Float positionIncrement) {
        reportData.setCreateHeader(configuration.createHeader());
        reportData.setHeaderStartRow(configuration.headerRowIndex());
        List<ReportHeaderCell> cells = new ArrayList<>();
        Function<Pair<Field, Column>, Void> columnFunction = AnnotationUtils.getHeadersFunction(cells, translations, positionIncrement);
        AnnotationUtils.fieldsWithColumnAnnotations(clazz, columnFunction, reportData.getName());

        for (SpecialColumn specialColumn : configuration.specialColumns()) {
            cells.add(new ReportHeaderCell(specialColumn.position(), specialColumn.title(), specialColumn.id(), specialColumn.autoSizeColumn()));
        }

        reportData
                .setHeader(new ReportHeader(configuration.sortableHeader()))
                .addCells(cells);
    }

    private <T> void loadRowsData(Collection<T> collection, Class<T> clazz, Float positionIncrement) {
        reportData.setDataStartRow(configuration.dataStartRowIndex());

        Map<Field, Column> columnsMap = new LinkedHashMap<>();
        Map<Field, Method> methodsMap = new LinkedHashMap<>();
        Function<Pair<Field, Column>, Void> columnFunction = AnnotationUtils.getFieldsAndColumnsFunction(columnsMap);

        AnnotationUtils.fieldsWithColumnAnnotations(clazz, columnFunction, reportData.getName());

        try {
            for (Map.Entry<Field, Column> entry : columnsMap.entrySet()) {
                methodsMap.put(entry.getKey(), ReflectionUtils.fetchFieldGetter(entry.getKey(), clazz));
            }
            for (T dto : collection) {
                ReportDataRow row = new ReportDataRow();
                for(Map.Entry<Field, Method> entry : methodsMap.entrySet()){
                    final Field field = entry.getKey();
                    final Method method = entry.getValue();
                    final Column column = columnsMap.get(field);
                    method.setAccessible(true);
                    final Object invokedValue = dto != null ? method.invoke(dto) : null;
                    ReportDataCell reportDataCell = new ReportDataCell(
                        column.position() + positionIncrement,
                        column.format(),
                        invokedValue,
                        column.valueType()
                    );
                    row.addCell(reportDataCell);
                }
                reportData.addRow(row);
            }
        } catch (IllegalAccessException e) {
            throw new ReportEngineReflectionException("Error invoking the method with no access", ILLEGAL_ACCESS);
        } catch (InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error invoking the method", INVOCATION_ERROR);
        }
    }

    private <T> void loadSubreports(Collection<T> collection, Class<T> clazz) throws IOException {
        final ReportDataParser reportDataParser = new ReportDataParser();
        Map<Field, Subreport> subreportMap = new LinkedHashMap<>();
        Function<Pair<Field, Subreport>, Void> subreportFunction = AnnotationUtils.getSubreportsFunction(subreportMap);
        AnnotationUtils.fieldsWithSubreportAnnotations(clazz, subreportFunction, reportData.getName());

        for (Map.Entry<Field, Subreport> entry : subreportMap.entrySet()) {
            final Field field = entry.getKey();
            final Method method = ReflectionUtils.fetchFieldGetter(field, clazz);
            final Subreport subreport = entry.getValue();
            Class<?> returnType = method.getReturnType();
            method.setAccessible(true);

            if(returnType.equals(List.class)){
                List<List<?>> subreportsList = new ArrayList<>();
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                returnType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                float subreportPositionalIncrement = AnnotationUtils.getSubreportLastColumn(returnType, reportData.getName()).position() + SUBREPORT_POSITIONAL_INCREMENT;

                for (T collectionEntry : collection) {
                    final Object invokeResult = subreportInvokeMethod(method, collectionEntry);
                    subreportsList.add((List<?>) invokeResult);
                }

                if(subreportsList.size() > 0){
                    float positionalIncrement = subreportPositionalIncrement;
                    final int subreportsInEveryList = subreportsList.get(0).size();
                    for (int i = 0; i < subreportsInEveryList; i++) {
                        final Collection<Object> subreportData = new ArrayList<>();
                        for (final List<?> list : subreportsList) {
                            subreportData.add(list.get(i));
                        }
                        parseSubreportData(reportDataParser, returnType, subreportData, positionalIncrement + subreport.position());
                        positionalIncrement += subreportPositionalIncrement;
                    }
                }
            } else {
                float subreportPositionalIncrement = AnnotationUtils.getSubreportLastColumn(returnType, reportData.getName()).position() + SUBREPORT_POSITIONAL_INCREMENT;
                final Collection<Object> subreportData = new ArrayList<>();
                for (T collectionEntry : collection) {
                    final Object invokeResult = subreportInvokeMethod(method, collectionEntry);
                    subreportData.add(invokeResult);
                }
                parseSubreportData(reportDataParser, returnType, subreportData, subreportPositionalIncrement + subreport.position());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void parseSubreportData(final ReportDataParser reportDataParser, final Class<?> returnType, final Collection subreportData, float positionalIncrement) throws IOException {
        final ReportData data = reportDataParser.parse(subreportData, reportData.getName(), returnType, positionalIncrement).getData();
        subreportsData.add(data);
    }

    private <T> Object subreportInvokeMethod(Method method, T collectionEntry){
        try {
            return method.invoke(collectionEntry);
        } catch (IllegalAccessException e) {
            throw new ReportEngineReflectionException("Error invoking the method with no access", ILLEGAL_ACCESS);
        } catch (InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error invoking the method", INVOCATION_ERROR);
        }
    }

    private <T> void loadSpecialColumns(Collection<T> collection, Class<T> clazz) {
        List<T> list = new ArrayList<>(collection);
        for (SpecialColumn specialColumn : configuration.specialColumns()) {
            Method method = null;
            if(ValueType.METHOD.equals(specialColumn.valueType())){
                method = ReflectionUtils.getMethodWithName(clazz, specialColumn.value(), new Class<?>[]{});
            }
            for (int i = 0; i < list.size(); i++) {
                Object value = specialColumn.value();
                try {
                    if(method != null) {
                        final T listElement = list.get(i);
                        method.setAccessible(true);
                        value = method.invoke(listElement);
                    }
                    reportData.getRows().get(i).addCell(new ReportDataCell(
                            specialColumn.position(),
                            specialColumn.format(),
                            value,
                            specialColumn.valueType()
                    ));
                } catch (IllegalAccessException e) {
                    throw new ReportEngineReflectionException("Error invoking the method with no access", ILLEGAL_ACCESS);
                } catch (InvocationTargetException e) {
                    throw new ReportEngineReflectionException("Error invoking the method", INVOCATION_ERROR);
                }
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

    private <T> void loadStyles(Class<T> clazz) {
        try {
            final List<Class<?>> interfaces = Arrays.asList(clazz.getInterfaces());
            if(interfaces.contains(StyledReport.class) || interfaces.contains(StripedRows.class)){
                final Constructor<T> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                final T newInstance = constructor.newInstance();
                if(interfaces.contains(StyledReport.class)){
                    StyledReport elem = (StyledReport) newInstance;
                    if(elem.getRangedRowStyles() != null){
                        reportData.getStyles().setRowStyles(elem.getRangedRowStyles().getOrDefault(reportData.getName(), null));
                    }
                    if(elem.getRangedColumnStyles() != null){
                        reportData.getStyles().setColumnStyles(elem.getRangedColumnStyles().getOrDefault(reportData.getName(), null));
                    }
                    if(elem.getPositionedStyles() != null){
                        reportData.getStyles().setPositionedStyles(elem.getPositionedStyles().getOrDefault(reportData.getName(), null));
                    }
                    if(elem.getRectangleRangedStyles() != null){
                        reportData.getStyles().setRangedStyleReportStyles(elem.getRectangleRangedStyles().getOrDefault(reportData.getName(), null));
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
        } catch (NoSuchMethodException e) {
            throw new ReportEngineReflectionException("Error obtaining the method reference" , NO_METHOD_ERROR);
        } catch (InstantiationException e) {
            throw new ReportEngineReflectionException("Error instantiating an object", INSTANTIATION_ERROR);
        } catch (IllegalAccessException e) {
            throw new ReportEngineReflectionException("Error instantiating an object with no access to the constructor", ILLEGAL_ACCESS);
        } catch (InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error instantiating an object with no parameter constructor", INVOCATION_ERROR);
        }
    }

    private void overrideSubreportsTitles(final Map<Integer, String> overriddenTitles) {
        for (final Map.Entry<Integer, String> entry : overriddenTitles.entrySet()) {
            this.reportData.getHeader().getCell(entry.getKey()).setTitle(entry.getValue());
        }
    }

    ReportData getData(){
        return reportData;
    }

}