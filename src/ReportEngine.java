import annotations.Report;
import annotations.ReportColumn;
import annotations.ReportColumns;
import annotations.ReportTemplate;
import cell.ReportDataColumn;
import cell.ReportHeaderCell;
import com.oracle.tools.packager.IOUtils;
import com.sun.istack.internal.NotNull;
import com.sun.javafx.scene.shape.PathUtils;
import data.ReportData;
import data.ReportDataRow;
import data.ReportHeader;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReportEngine<T> {

    private ReportData reportData;
    private T firstElement;
    private Collection<ReportDataColumn> emptyColumns;
    private Report reportAnnotation;
    private ReportTemplate reportTemplate;

    public ReportEngine parse(@NotNull T dto, @NotNull final String reportName) throws Exception {
        return parse(Collections.singletonList(dto), reportName);
    }

    public ReportEngine parse(@NotNull Collection<T> collection, @NotNull final String reportName) throws Exception {
        reportData = new ReportData(reportName);
        firstElement = collection.iterator().next();
        checkCollectionNotEmpty(collection);
        reportAnnotation = getReportAnnotation(this.reportData, firstElement);
        checkReportAnnotation(firstElement);
        loadReportTemplate();
        loadReportHeader(firstElement);
        loadReportRows(reportData, collection);
        return this;
    }

    private void loadReportTemplate() throws Exception {
        reportTemplate = asList(reportAnnotation.templates())
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
        ReportHeader reportHeader = new ReportHeader();
        Function<AbstractMap.SimpleEntry<Method, ReportColumn>, Void> columnFunction = list -> {
            ReportColumn column = list.getValue();
            reportHeader.addCell(new ReportHeaderCell(column.position(), column.title()));
            return null;
        };
        loadMethodsColumns(dto, columnFunction);
        reportData.setHeader(reportHeader)
                .addCells(ReportHeaderCell.from(emptyColumns))
                .sortCells();
    }

    private void loadReportRows(ReportData reportData, Collection<T> collection) throws Exception {
        loadEmptyColumns();
        loadRows(reportData, collection);
    }

    private void loadRows(ReportData reportData, Collection<T> collection) throws Exception {
        loadRowColumns(collection);
        reportData.orderColumns();
    }

    private void loadRowColumns(Collection<T> collection) throws Exception {
        Map<Method, ReportColumn> methodsMap = new LinkedHashMap<>();
        Function<AbstractMap.SimpleEntry<Method, ReportColumn>, Void> columnFunction = list -> {
            Method method = list.getKey();
            ReportColumn column = list.getValue();
            methodsMap.put(method, column);
            return null;
        };

        loadMethodsColumns(collection.iterator().next(), columnFunction);

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

    public File generateReport() throws Exception {
        if(Objects.isNull(reportData.getTemplateInputStream())){
            throw new Exception("There is no @ReportTemplate defined in @Report annotation for \"" + reportData.getName() + "\" report name");
        }
        // TODO create file from template
        // TODO insert data to created file
        return null;
    }

    private static <T> List<T> asList(T[] array) {
        return new ArrayList<>(Arrays.asList(array));
    }

}
