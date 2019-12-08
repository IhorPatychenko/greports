import annotations.Report;
import annotations.ReportColumn;
import annotations.ReportColumns;
import annotations.ReportTemplate;
import content.cell.ReportCell;
import content.cell.ReportDataColumn;
import content.cell.ReportHeaderCell;
import content.ReportData;
import content.ReportDataRow;
import content.ReportHeader;
import styles.interfaces.StripedRows;
import styles.interfaces.StyledReport;
import positioning.TranslationsParser;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        reportData = new ReportData(reportName);
        T firstElement = collection.iterator().next();
        checkCollectionNotEmpty(collection);
        reportAnnotation = getReportAnnotation(this.reportData, firstElement);
        translations = new TranslationsParser(this.reportAnnotation.translationsDir()).parse(this.reportLang);
        checkReportAnnotation(firstElement);
        loadReportTemplate();
        loadReportHeader(firstElement);
        loadReportRows(collection);
        loadReportStyles(firstElement);
        return this;
    }

    private void loadReportTemplate() throws Exception {
        final ReportTemplate reportTemplate = asList(reportAnnotation.templates())
                .stream()
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
            loadMethodsColumns(dto, columnFunction);
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

        loadMethodsColumns(collection.iterator().next(), columnFunction);

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
                            invokedValue
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
                        .setStripedRowsIndex(elem.getStripedRowsIndex())
                        .setStripedRowsColor(elem.getStripedRowsColor());
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
        emptyColumns = asList(reportAnnotation.emptyColumns())
                .stream()
                .filter(column -> reportData.getName().equals(column.reportName()))
                .map(column -> new ReportDataColumn(
                        column.position(),
                        (String) translations.getOrDefault(column.title(), column.title()),
                        null,
                        null
                )).collect(Collectors.toList());
    }

    private static <T> Report getReportAnnotation(ReportData reportData, T dto) {
        Annotation[] classAnnotations = dto.getClass().getAnnotations();
        List<Annotation> dtoAnnotations = asList(classAnnotations);
        return (Report) (dtoAnnotations.stream()
                .filter(entry -> entry instanceof Report && asList(((Report) entry).name()).contains(reportData.getName()))
                .findFirst()
                .orElse(null));
    }

    private <T> void loadMethodsColumns(T dto, Function<AbstractMap.SimpleEntry<Method, ReportColumn>, Void> columnFunction){
        for (Method method : dto.getClass().getMethods()) {
            for (Annotation annotation : method.getDeclaredAnnotations()) {
                if(annotation instanceof ReportColumn && getMethodAnnotationPredicate(reportData).test(annotation)){
                    columnFunction.apply(new AbstractMap.SimpleEntry<>(method, (ReportColumn) annotation));
                } else if(annotation instanceof ReportColumns){
                    Optional<ReportColumn> first = asList(((ReportColumns) annotation).value()).stream()
                            .filter(column -> getMethodAnnotationPredicate(reportData).test(column))
                            .findFirst();
                    first.ifPresent(column -> columnFunction.apply(new AbstractMap.SimpleEntry<>(method, column)));
                }
            }
        }
    }

    private static Predicate<Annotation> getMethodAnnotationPredicate(ReportData reportData) {
        return annotation -> annotation instanceof ReportColumn && ((ReportColumn) annotation).reportName().equals(reportData.getName());
    }

    private <T> void checkCollectionNotEmpty(Collection<T> collection) throws Exception {
        if (collection.isEmpty()) {
            throw new Exception("The collection cannot be empty");
        }
    }

    private <T> void checkReportAnnotation(T dto) throws Exception {
        if (Objects.isNull(reportAnnotation)) {
            throw new Exception(dto.getClass().toString() + " is not annotated as @Report or has no name \"" + reportData.getName() + "\"");
        } else if(reportAnnotation.headerOffset() >= reportAnnotation.dataOffset()){
            throw new Exception("Header offset cannot be greater or equals than data offset");
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

    private static <T> List<T> asList(T[] array) {
        return new ArrayList<>(Arrays.asList(array));
    }

}
