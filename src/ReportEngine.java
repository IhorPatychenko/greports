import annotations.Report;
import annotations.ReportColumn;
import annotations.ReportColumns;
import com.sun.istack.internal.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReportEngine {

    public static <T> ReportData parse(@NotNull final T dto, @NotNull final String reportName) throws Exception {
        return parse(dto, reportName, true);
    }

    public static <T> ReportData parse(@NotNull final T dto, @NotNull final String reportName, boolean extractValues) throws Exception {
        return parse(Collections.singletonList(dto), reportName, extractValues);
    }

    public static <T> ReportData parse(@NotNull final Collection<T> collection, @NotNull final String reportName) throws Exception {
        return parse(collection, reportName, true);
    }

    public static <T> ReportData parse(@NotNull final Collection<T> collection, @NotNull final String reportName, boolean extractValues) throws Exception {
        ReportData reportData = new ReportData(reportName);
        checkCollectionNotEmpty(collection);
        checkReportAnnotation(reportData, collection.iterator().next());
        loadReportHeader(reportData, collection.iterator().next());
        loadReportRows(reportData, collection, extractValues);
        return reportData;
    }

    private static <T> void loadReportHeader(ReportData reportData, T dto) {
        Collection<ReportDataColumn> emptyColumns = loadEmptyColumns(reportData, dto);
        ReportHeader reportHeader = new ReportHeader();
        Function<AbstractMap.SimpleEntry<Method, ReportColumn>, Void> columnFunction = list -> {
            ReportColumn column = list.getValue();
            reportHeader.addCell(new ReportHeaderCell(column.position(), column.title()));
            return null;
        };
        loadMethodsColumns(reportData, dto, columnFunction);
        reportData.setHeader(reportHeader)
                .addCells(ReportHeaderCell.from(emptyColumns))
                .sortCells();
    }

    private static <T> void loadReportRows(ReportData reportData, Collection<T> collection, boolean extractValues) throws Exception {
        Collection<ReportDataColumn> emptyColumns = loadEmptyColumns(reportData, collection.iterator().next());
        for (T dto : collection) {
            loadRow(reportData, dto, emptyColumns, extractValues);
        }
    }

    private static <T> void loadRow(ReportData reportData, T dto, Collection<ReportDataColumn> emptyColumns, boolean extractValues) throws Exception {
        loadRowColumns(reportData, dto, emptyColumns, extractValues);
        reportData.orderColumns();
    }

    private static <T> void loadRowColumns(ReportData reportData, T dto, Collection<ReportDataColumn> emptyColumns, boolean extractValues) throws Exception {
        Map<Method, ReportColumn> methodsMap = new LinkedHashMap<>();
        Function<AbstractMap.SimpleEntry<Method, ReportColumn>, Void> columnFunction = list -> {
            Method method = list.getKey();
            ReportColumn column = list.getValue();
            methodsMap.put(method, column);
            return null;
        };

        loadMethodsColumns(reportData, dto, columnFunction);

        ReportDataRow row = new ReportDataRow();
        for(Map.Entry<Method, ReportColumn> entry : methodsMap.entrySet()){
            ReportDataColumn reportDataColumn = new ReportDataColumn(entry.getValue().position(), entry.getValue().title());
            if (extractValues) {
                try {
                    reportDataColumn.setValue(entry.getKey().invoke(dto));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new Exception("Error obtaining the value of column");
                }
            }
            row.addColumn(reportDataColumn);
        }
        emptyColumns.forEach(row::addColumn);
        reportData.addRow(row);
    }

    private static <T> Collection<ReportDataColumn> loadEmptyColumns(ReportData reportData, T dto) {
        Report reportAnnotation = getReportAnnotation(reportData, dto);

        return asList(reportAnnotation.emptyColumns())
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

    private static <T> void loadMethodsColumns(ReportData reportData, T dto, Function<AbstractMap.SimpleEntry<Method, ReportColumn>, Void> columnFunction){
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

    private static <T> void checkCollectionNotEmpty(Collection<T> collection) throws Exception {
        if (collection.isEmpty()) {
            throw new Exception("The collection cannot be empty");
        }
    }

    private static <T> void checkReportAnnotation(ReportData reportData, T dto) throws Exception {
        Annotation[] classAnnotations = dto.getClass().getAnnotations();
        List<Annotation> dtoAnnotations = asList(classAnnotations);

        boolean containsReportAnnotation = dtoAnnotations.stream().anyMatch(entry -> entry instanceof Report);
        if (!containsReportAnnotation) {
            throw new Exception(dto.getClass().toString() + " is not annotated as @Report");
        }

        Optional<Annotation> optionalReportAnnotation = dtoAnnotations.stream()
                .filter(entry -> entry instanceof Report)
                .filter(entry -> {
                    List<String> names = asList(((Report) entry).name());
                    return names.contains(reportData.getName());
                })
                .findFirst();
        if (!optionalReportAnnotation.isPresent()) {
            throw new Exception(dto.getClass().toString() + " has no name '" + reportData.getName() + "'");
        }
    }

    private static <T> List<T> asList(T[] array) {
        return new ArrayList<>(Arrays.asList(array));
    }

}
