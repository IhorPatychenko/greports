import annotations.Report;
import annotations.ReportColumn;
import annotations.ReportColumns;
import annotations.ReportTemplate;
import cell.ReportCell;
import cell.ReportDataColumn;
import cell.ReportHeaderCell;
import data.ReportData;
import data.ReportDataRow;
import data.ReportHeader;
import utils.YamlParser;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ReportEngine<T> {

    private String reportLang;
    private ReportData reportData;
    private Collection<ReportDataColumn> emptyColumns;
    private Report reportAnnotation;

    public ReportEngine(){
        this("en");
    }

    public ReportEngine(String lang) {
        this.reportLang = lang;
    }

    public ReportEngine parse(T dto, final String reportName) throws Exception {
        return parse(Collections.singletonList(dto), reportName);
    }

    public ReportEngine parse(Collection<T> collection, final String reportName) throws Exception {
        reportData = new ReportData(reportName);
        T firstElement = collection.iterator().next();
        checkCollectionNotEmpty(collection);
        reportAnnotation = getReportAnnotation(this.reportData, firstElement);
        checkReportAnnotation(firstElement);
        loadReportTemplate();
        loadReportHeader(firstElement);
        loadReportRows(collection);
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

    private void loadReportHeader(T dto) {
        loadEmptyColumns();
        List<ReportHeaderCell> cells = new ArrayList<>();
        final Map<String, Object> titles = new YamlParser().parse(this.reportAnnotation.translationsDir(), this.reportLang);
        Function<AbstractMap.SimpleEntry<Method, ReportColumn>, Void> columnFunction = list -> {
            ReportColumn column = list.getValue();
            cells.add(new ReportHeaderCell(column.position(), (String) titles.getOrDefault(column.title(), column.title())));
            return null;
        };
        loadMethodsColumns(dto, columnFunction);
        cells.addAll(ReportHeaderCell.from(emptyColumns));
        cells.sort(Comparator.comparing(ReportCell::getPosition));
        reportData.setHeader(new ReportHeader())
                .addCells(cells);
    }

    private void loadReportRows(Collection<T> collection) throws Exception {
        loadEmptyColumns();
        loadRowColumns(collection);
    }

    private void loadRowColumns(Collection<T> collection) throws Exception {
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

        for (T dto : collection) {
            ReportDataRow row = new ReportDataRow();
            for(Map.Entry<Method, ReportColumn> entry : methodsMap.entrySet()){
                ReportDataColumn reportDataColumn = new ReportDataColumn(entry.getValue().position(), entry.getValue().title());
                try {
                    reportDataColumn.setValue(entry.getKey().invoke(dto));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new Exception("Error obtaining the value of column");
                }
                row.addColumn(reportDataColumn);
            }
            emptyColumns.forEach(row::addColumn);
            reportData.addRow(row);
        }
    }

    private Map<Method, ReportColumn> sortMethodsMapByColumnOrder(Map<Method, ReportColumn> methodsMap) {
        List<Map.Entry<Method, ReportColumn>> list = new ArrayList<>(methodsMap.entrySet());
        return list
                .stream()
                .sorted(Comparator.comparing(o -> o.getValue().position()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
    }

    private void sortHeaderColumns(){

    }

    private void loadEmptyColumns() {
        emptyColumns = asList(reportAnnotation.emptyColumns())
                .stream()
                .filter(column -> reportData.getName().equals(column.reportName()))
                .map(column -> new ReportDataColumn(column.position(), column.title(), column.value()))
                .collect(Collectors.toList());
    }

    private static <T> Report getReportAnnotation(ReportData reportData, T dto) {
        Annotation[] classAnnotations = dto.getClass().getAnnotations();
        List<Annotation> dtoAnnotations = asList(classAnnotations);
        return (Report) (dtoAnnotations.stream()
                .filter(entry -> entry instanceof Report && asList(((Report) entry).name()).contains(reportData.getName()))
                .findFirst()
                .orElse(null));
    }

    private void loadMethodsColumns(T dto, Function<AbstractMap.SimpleEntry<Method, ReportColumn>, Void> columnFunction){
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

    private void checkCollectionNotEmpty(Collection<T> collection) throws Exception {
        if (collection.isEmpty()) {
            throw new Exception("The collection cannot be empty");
        }
    }

    private void checkReportAnnotation(T dto) throws Exception {
        if (Objects.isNull(this.reportAnnotation)) {
            throw new Exception(dto.getClass().toString() + " is not annotated as @Report or has no name \"" + reportData.getName() + "\"");
        }
    }

    public ReportData getData(){
        return reportData;
    }

    private static <T> List<T> asList(T[] array) {
        return new ArrayList<>(Arrays.asList(array));
    }

}
