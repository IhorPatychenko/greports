package org.greports.engine;

import org.greports.annotations.Column;
import org.greports.annotations.Configuration;
import org.greports.annotations.SpecialColumn;
import org.greports.annotations.SpecialRowCell;
import org.greports.annotations.SpecialRow;
import org.greports.annotations.Subreport;
import org.greports.content.column.ReportDataCell;
import org.greports.content.cell.ReportDataSpecialRowCell;
import org.greports.content.cell.ReportHeaderCell;
import org.greports.content.ReportData;
import org.greports.content.row.ReportDataRow;
import org.greports.content.ReportHeader;
import org.greports.content.row.ReportDataSpecialRow;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.exceptions.ReportEngineRuntimeException;
import org.greports.positioning.VerticalRange;
import org.greports.styles.ReportDataStyles;
import org.greports.styles.interfaces.ConditionalRowStyles;
import org.greports.styles.interfaces.StripedRows;
import org.greports.styles.interfaces.StyledReport;
import org.greports.positioning.TranslationsParser;
import org.greports.styles.stylesbuilders.AbstractReportStylesBuilder;
import org.greports.styles.stylesbuilders.VerticalRangedStyleBuilder;
import org.greports.styles.stylesbuilders.VerticalRangedStylesBuilder;
import org.greports.utils.AnnotationUtils;
import org.greports.utils.Pair;
import org.greports.utils.ReflectionUtils;
import org.greports.utils.Translator;
import org.greports.utils.Utils;

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

final class ReportDataParser {

    private ReportData reportData;
    private Translator translator;
    private Configuration configuration;
    private List<ReportData> subreportsData = new ArrayList<>();
    private static final float SUBREPORT_POSITIONAL_INCREMENT = 0.00000000000001f;

    protected <T> ReportDataParser parse(Collection<T> collection, final String reportName, Class<T> clazz, ReportConfigurator configurator) throws ReportEngineReflectionException, ReportEngineRuntimeException {
        final ReportDataParser parser = parse(collection, reportName, clazz, 0f);
        overrideSubreportsTitles(configurator.getOverriddenTitles());
        return parser;
    }

    private <T> ReportDataParser parse(Collection<T> collection, final String reportName, Class<T> clazz, Float positionIncrement) throws ReportEngineReflectionException, ReportEngineRuntimeException {
        configuration = AnnotationUtils.getReportConfiguration(clazz, reportName);
        reportData = new ReportData(reportName, configuration.sheetName(), !configuration.templatePath().equals("") ? getClass().getClassLoader().getResource(configuration.templatePath()) : null);
        final Map<String, Object> translations = new TranslationsParser(configuration.translationsDir()).parse(Utils.getLocale(configuration.locale()).getLanguage());
        translator = new Translator(translations);
        subreportsData = new ArrayList<>();
        parseReportHeader(clazz, positionIncrement);
        parseRowsData(collection, clazz, positionIncrement);
        parseSpecialColumns(collection, clazz);
        parseSpecialRows();
        parseStyles(collection, clazz);
        parseSubreports(collection, clazz);
        reportData.mergeReportData(subreportsData);
        return this;
    }

    private <T> void parseReportHeader(Class<T> clazz, Float positionIncrement) {
        reportData.setCreateHeader(configuration.createHeader());
        reportData.setHeaderStartRow(configuration.headerRowIndex());
        List<ReportHeaderCell> cells = new ArrayList<>();
        Function<Pair<Field, Column>, Void> columnFunction = AnnotationUtils.getHeadersFunction(cells, translator, positionIncrement);
        AnnotationUtils.fieldsWithColumnAnnotations(clazz, columnFunction, reportData.getName());

        for (SpecialColumn specialColumn : configuration.specialColumns()) {
            cells.add(new ReportHeaderCell(specialColumn.position(), specialColumn.title(), specialColumn.id(), specialColumn.autoSizeColumn()));
        }

        reportData
                .setHeader(new ReportHeader(configuration.sortableHeader()))
                .addCells(cells);
    }

    private <T> void parseRowsData(Collection<T> collection, Class<T> clazz, Float positionIncrement) throws ReportEngineReflectionException {
        reportData.setDataStartRow(configuration.dataStartRowIndex());

        Map<Field, Column> columnsMap = new LinkedHashMap<>();
        Map<Field, Method> methodsMap = new LinkedHashMap<>();
        Function<Pair<Field, Column>, Void> columnFunction = AnnotationUtils.getFieldsAndColumnsFunction(columnsMap);

        AnnotationUtils.fieldsWithColumnAnnotations(clazz, columnFunction, reportData.getName());

        Method method;
        try {
            for (Map.Entry<Field, Column> entry : columnsMap.entrySet()) {
                methodsMap.put(entry.getKey(), ReflectionUtils.fetchFieldGetter(entry.getKey(), clazz));
            }
            for (T dto : collection) {
                ReportDataRow row = new ReportDataRow();
                for(Map.Entry<Field, Method> entry : methodsMap.entrySet()){
                    final Field field = entry.getKey();
                    method = entry.getValue();
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
            throw new ReportEngineReflectionException("Error invoking the method with no access", e, clazz);
        } catch (InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error invoking the method", e, clazz);
        }
    }

    private <T> void parseSubreports(Collection<T> collection, Class<T> clazz) throws ReportEngineReflectionException {
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
            Class<?> componentType = returnType;

            if(returnType.isArray() || returnType.equals(List.class)){

                List<List<?>> subreportsList = new ArrayList<>();
                if(returnType.isArray()){
                    componentType = returnType.getComponentType();
                } else {
                    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                    componentType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                }
                float subreportPositionalIncrement = Math.max(AnnotationUtils.getSubreportLastColumn(componentType, reportData.getName()).position(), SUBREPORT_POSITIONAL_INCREMENT) + SUBREPORT_POSITIONAL_INCREMENT;

                for (T collectionEntry : collection) {
                    final Object invokeResult = subreportInvokeMethod(method, collectionEntry);
                    if(returnType.isArray()){
                        subreportsList.add(new ArrayList<>(Arrays.asList((Object[]) invokeResult)));
                    } else {
                        subreportsList.add((List) invokeResult);
                    }
                }

                if(subreportsList.size() > 0){
                    float positionalIncrement = subreportPositionalIncrement;
                    final int subreportsInEveryList = subreportsList.get(0).size();
                    for (int i = 0; i < subreportsInEveryList; i++) {
                        final Collection<Object> subreportData = new ArrayList<>();
                        for (final List<?> list : subreportsList) {
                            subreportData.add(list.get(i));
                        }
                        parseSubreportData(reportDataParser, componentType, subreportData, positionalIncrement + subreport.position());
                        positionalIncrement += subreportPositionalIncrement;
                    }
                }
            } else {
                float subreportPositionalIncrement = AnnotationUtils.getSubreportLastColumn(componentType, reportData.getName()).position() + SUBREPORT_POSITIONAL_INCREMENT;
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
    private void parseSubreportData(final ReportDataParser reportDataParser, final Class<?> returnType, final Collection subreportData, float positionalIncrement) throws ReportEngineReflectionException {
        final ReportData data = reportDataParser.parse(subreportData, reportData.getName(), returnType, positionalIncrement).getData();
        subreportsData.add(data);
    }

    private <T> Object subreportInvokeMethod(Method method, T collectionEntry) throws ReportEngineReflectionException {
        try {
            return method.invoke(collectionEntry);
        } catch (IllegalAccessException e) {
            throw new ReportEngineReflectionException("Error invoking the method with no access", e, method.getDeclaringClass());
        } catch (InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error invoking the method", e, method.getDeclaringClass());
        }
    }

    private <T> void parseSpecialColumns(Collection<T> collection, Class<T> clazz) throws ReportEngineReflectionException {
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
                    throw new ReportEngineReflectionException("Error invoking the method with no access", e, clazz);
                } catch (InvocationTargetException e) {
                    throw new ReportEngineReflectionException("Error invoking the method", e, clazz);
                }
            }
        }
    }

    private void parseSpecialRows(){
        for(SpecialRow specialRow : configuration.specialRows()){
            final ReportDataSpecialRow reportDataSpecialRow = new ReportDataSpecialRow(specialRow.rowIndex());
            for (final SpecialRowCell column : specialRow.cells()) {
                reportDataSpecialRow.addCell(new ReportDataSpecialRowCell(column.valueType(), column.value(), column.format(), column.targetId()));
            }
            reportData.addSpecialRow(reportDataSpecialRow);
        }
    }

    private <T> void parseStyles(Collection<T> collection, Class<T> clazz) throws ReportEngineReflectionException {
        try {
            final Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            final T newInstance = constructor.newInstance();
            final ReportDataStyles reportDataStyles = reportData.getStyles();
            if(newInstance instanceof StyledReport){
                final StyledReport instance = (StyledReport) newInstance;
                if(instance.getRangedRowStyles() != null){
                    reportDataStyles.setRowStyles(instance.getRangedRowStyles().getOrDefault(reportData.getName(), null));
                }
                if(instance.getRangedColumnStyles() != null){
                    reportDataStyles.setColumnStyles(instance.getRangedColumnStyles().getOrDefault(reportData.getName(), null));
                }
                if(instance.getPositionedStyles() != null){
                    reportDataStyles.setPositionedStyles(instance.getPositionedStyles().getOrDefault(reportData.getName(), null));
                }
                if(instance.getRectangleRangedStyles() != null){
                    reportDataStyles.setRectangleStyles(instance.getRectangleRangedStyles().getOrDefault(reportData.getName(), null));
                }
            }
            if(newInstance instanceof StripedRows){
                final StripedRows instance = (StripedRows) newInstance;
                if(instance.getStripedRowsIndex() != null && instance.getStripedRowsColor() != null){
                    reportDataStyles
                            .setStripedRowsIndex(instance.getStripedRowsIndex().getOrDefault(reportData.getName(), null))
                            .setStripedRowsColor(instance.getStripedRowsColor().getOrDefault(reportData.getName(), null));
                }
            }
            if(newInstance instanceof ConditionalRowStyles){
                final List<T> list = new ArrayList<>(collection);
                final short startRowIndex = configuration.dataStartRowIndex();
                for (int i = 0; i < list.size(); i++) {
                    final T entry = list.get(i);
                    final ConditionalRowStyles conditionalRowStyles = (ConditionalRowStyles) entry;
                    if(conditionalRowStyles.isStyled(i)){
                        VerticalRangedStyleBuilder styleBuilder;
                        if(null != (styleBuilder = conditionalRowStyles.getIndexBasedStyle().get(reportData.getName()))){
                            styleBuilder.setTuple(new VerticalRange(startRowIndex + i, startRowIndex + i));
                            VerticalRangedStylesBuilder verticalRangedStylesBuilder = reportDataStyles.getRowStyles();
                            if(verticalRangedStylesBuilder == null){
                                verticalRangedStylesBuilder = new VerticalRangedStylesBuilder(AbstractReportStylesBuilder.StylePriority.PRIORITY4);
                                reportDataStyles.setRowStyles(verticalRangedStylesBuilder);
                            }
                            verticalRangedStylesBuilder.addStyleBuilder(styleBuilder);
                        }
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            throw new ReportEngineReflectionException("Error obtaining the method reference", e, clazz);
        } catch (InstantiationException e) {
            throw new ReportEngineReflectionException("Error instantiating an object", e, clazz);
        } catch (IllegalAccessException e) {
            throw new ReportEngineReflectionException("Error instantiating an object with no access to the constructor", e, clazz);
        } catch (InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error instantiating an object with no parameter constructor", e, clazz);
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