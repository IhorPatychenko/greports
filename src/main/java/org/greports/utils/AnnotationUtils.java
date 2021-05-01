package org.greports.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.greports.annotations.Cell;
import org.greports.annotations.CellGetter;
import org.greports.annotations.Column;
import org.greports.annotations.ColumnGetter;
import org.greports.annotations.ColumnSetter;
import org.greports.annotations.Configuration;
import org.greports.annotations.Report;
import org.greports.annotations.SpecialColumn;
import org.greports.annotations.Subreport;
import org.greports.annotations.SubreportGetter;
import org.greports.annotations.SubreportSetter;
import org.greports.content.cell.HeaderCell;
import org.greports.engine.ReportBlock;
import org.greports.engine.ReportConfiguration;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.exceptions.ReportEngineRuntimeException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Annotation utils class. This one is for internal use of greports engine
 */
public class AnnotationUtils {

    private AnnotationUtils() {}

    private static Report getReportAnnotation(Class<?> clazz) {
        return Optional.ofNullable(clazz.getAnnotation(Report.class))
                .orElseThrow(() -> new ReportEngineRuntimeException(String.format("%s class has no %s.Report annotation", clazz.toString(), Report.class.getPackage().getName()), clazz));
    }

    public static Configuration getReportConfiguration(Class<?> clazz, String reportName) {
        final Report report = getReportAnnotation(clazz);
        return Arrays.stream(report.reportConfigurations())
            .filter(entry -> Arrays.asList(entry.reportName()).contains(reportName))
            .findFirst()
            .orElseThrow(() -> new ReportEngineRuntimeException(
                String.format("%s has no %s annotation with name \"%s\"", Report.class.getName(), Configuration.class.getName(), reportName),
                report.getClass()
            ));
    }

    public static int getLastSpecialRowsCount(ReportConfiguration configuration) {
        return (int) configuration.getSpecialRows().stream().filter(entry -> entry.getRowIndex() == Integer.MAX_VALUE).count();
    }

    public static <T> void methodsWithColumnAnnotations(Class<T> clazz, Function<Pair<Column, Method>, Void> columnFunction, String reportName) throws ReportEngineReflectionException {
        for (Field declaredField : getAllClassFields(clazz)) {
            final Column[] columns = declaredField.getAnnotationsByType(Column.class);
            for (Column column : columns) {
                if (getReportColumnPredicate(reportName).test(column)) {
                    final Method method = ReflectionUtils.fetchFieldGetter(declaredField, clazz);
                    columnFunction.apply(Pair.of(column, method));
                }
            }
        }

        for (final Method declaredMethod : getAllClassMethods(clazz)) {
            final ColumnGetter[] columnGetters = declaredMethod.getAnnotationsByType(ColumnGetter.class);
            for (final ColumnGetter columnGetter : columnGetters) {
                final Column column = AnnotationsConverter.convert(columnGetter);
                if (getReportColumnPredicate(reportName).test(column)) {
                    columnFunction.apply(Pair.of(column, declaredMethod));
                }
            }
        }
    }


    public static <T> void cellsWithMethodsFunction(Class<T> clazz, Function<Pair<Cell, Method>, Void> cellFunction, String reportName) throws ReportEngineReflectionException {
        for (Field declaredField : getAllClassFields(clazz)) {
            final Cell[] cells = declaredField.getAnnotationsByType(Cell.class);
            for (Cell cell : cells) {
                if (getReportCellPredicate(reportName).test(cell)) {
                    final Method method = ReflectionUtils.fetchFieldGetter(declaredField, clazz);
                    cellFunction.apply(Pair.of(cell, method));
                }
            }
        }

        for (final Method declaredMethod : getAllClassMethods(clazz)) {
            final CellGetter[] cellGetters = declaredMethod.getAnnotationsByType(CellGetter.class);
            for (final CellGetter cellGetter : cellGetters) {
                final Cell cell = AnnotationsConverter.convert(cellGetter);
                if (getReportCellPredicate(reportName).test(cell)) {
                    cellFunction.apply(Pair.of(cell, declaredMethod));
                }
            }
        }
    }

    private static <T> List<Field> getAllClassFields(Class<T> clazz){
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        if(clazz.getSuperclass() != null){
            fields.addAll(getAllClassFields(clazz.getSuperclass()));
        }
        return fields;
    }

    private static <T> List<Method> getAllClassMethods(Class<T> clazz) {
        List<Method> methods = new ArrayList<>(Arrays.asList(clazz.getDeclaredMethods()));
        if(clazz.getSuperclass() != null){
            methods.addAll(getAllClassMethods(clazz.getSuperclass()));
        }
        return methods;
    }

    public static <T> Column getSubreportLastColumn(Class<T> clazz, String reportName) {
        List<Column> list = new ArrayList<>();
        final List<Field> fields = getAllClassFields(clazz);
        for (final Field field : fields) {
            final Column[] columns = field.getAnnotationsByType(Column.class);
            for (final Column columnAnnotation : columns) {
                if (getReportColumnPredicate(reportName).test(columnAnnotation)) {
                    list.add(columnAnnotation);
                }
            }
        }

        final List<Method> methods = getAllClassMethods(clazz);
        for (final Method method : methods) {
            final ColumnGetter[] columnGetters = method.getAnnotationsByType(ColumnGetter.class);
            for (final ColumnGetter columnGetter : columnGetters) {
                final Column column = AnnotationsConverter.convert(columnGetter);
                if (getReportColumnPredicate(reportName).test(column)) {
                    list.add(column);
                }
            }
        }
        return list.stream().max(Comparator.comparing(Column::position)).orElse(null);
    }

    public static Map<Annotation, Method> loadBlockAnnotations(final ReportBlock reportBlock) throws ReportEngineReflectionException {
        Map<Annotation, Method> map = new HashMap<>();
        Class<?> clazz = reportBlock.getBlockClass();
        final List<Field> fields = getAllClassFields(clazz);
        for (final Field field : fields) {
            final Optional<Subreport> optionalSubreport = Arrays.stream(field.getAnnotationsByType(Subreport.class))
                    .filter(subreport -> Arrays.asList(subreport.reportName()).contains(reportBlock.getReportName()))
                    .findFirst();
            if(optionalSubreport.isPresent()){
                final Subreport subreport = optionalSubreport.get();
                map.put(subreport, ReflectionUtils.fetchFieldSetter(field, clazz));
            }

            final Optional<Column> optionalColumn = Arrays.stream(field.getAnnotationsByType(Column.class))
                    .filter(column -> Arrays.asList(column.reportName()).contains(reportBlock.getReportName()))
                    .findFirst();
            if(optionalColumn.isPresent()) {
                final Column column = optionalColumn.get();
                map.put(column, ReflectionUtils.fetchFieldSetter(field, clazz));
            }
        }

        final List<Method> methods = getAllClassMethods(clazz);
        for (final Method method : methods) {
            final Optional<SubreportSetter> optionalSubreport = Arrays.stream(method.getAnnotationsByType(SubreportSetter.class))
                    .filter(subreport -> Arrays.asList(subreport.reportName()).contains(reportBlock.getReportName()))
                    .findFirst();
            if(optionalSubreport.isPresent()){
                final Subreport subreport = AnnotationsConverter.convert(optionalSubreport.get());
                map.put(subreport, method);
            }

            final Optional<ColumnSetter> optionalColumn = Arrays.stream(method.getAnnotationsByType(ColumnSetter.class))
                    .filter(column -> Arrays.asList(column.reportName()).contains(reportBlock.getReportName()))
                    .findFirst();
            if(optionalColumn.isPresent()) {
                final Column column = AnnotationsConverter.convert(optionalColumn.get());
                map.put(column, method);
            }
        }

        Configuration configuration = getReportConfiguration(clazz, reportBlock.getReportName());
        for (SpecialColumn specialColumn : configuration.specialColumns()) {
            map.put(specialColumn, null);
        }
        return map;
    }

    public static <T> void methodsWithSubreportAnnotations(Class<T> clazz, Function<Pair<Subreport, Method>, Void> columnFunction, String reportName) throws ReportEngineReflectionException {
        for (Field field : getAllClassFields(clazz)) {
            final Subreport[] subreports = field.getAnnotationsByType(Subreport.class);
            for (Subreport subreport : subreports) {
                if (getSubreportPredicate(reportName).test(subreport)) {
                    final Method method = ReflectionUtils.fetchFieldGetter(field, clazz);
                    columnFunction.apply(Pair.of(subreport, method));
                }
            }
        }

        for (final Method method : getAllClassMethods(clazz)) {
            final SubreportGetter[] subreportGetters = method.getAnnotationsByType(SubreportGetter.class);
            for (final SubreportGetter subreportGetter : subreportGetters) {
                final Subreport subreport = AnnotationsConverter.convert(subreportGetter);
                if (getSubreportPredicate(reportName).test(subreport)) {
                    columnFunction.apply(Pair.of(subreport, method));
                }
            }
        }
    }

    private static Predicate<Annotation> getReportColumnPredicate(String reportName) {
        return annotation -> Arrays.asList(((Column) annotation).reportName()).contains(reportName);
    }

    private static Predicate<Annotation> getReportCellPredicate(String reportName) {
        return annotation -> Arrays.asList(((Cell) annotation).reportName()).contains(reportName);
    }

    private static Predicate<Annotation> getSubreportPredicate(String reportName) {
        return annotation -> Arrays.asList(((Subreport) annotation).reportName()).contains(reportName);
    }

    public static Function<Pair<Column, Method>, Void> getMethodsAndColumnsFunction(Map<Column, Method> columnsMap) {
        return pair -> {
            columnsMap.put(pair.getLeft(), pair.getRight());
            return null;
        };
    }

    public static Function<Pair<Cell, Method>, Void> getCellsAndMethodsFunction(Map<Cell, Method> cellMap){
        return pair -> {
            cellMap.put(pair.getLeft(), pair.getRight());
            return null;
        };
    }

    public static Function<Pair<Column, Method>, Void> getHeadersFunction(List<HeaderCell> cells, Translator translator, Float positionIncrement, String idPrefix) {
        return pair -> {
            Column column = pair.getLeft();
            cells.add(new HeaderCell(
                    column.position() + positionIncrement,
                    translator.translate(column.title()),
                    Utils.generateId(idPrefix, column.id()),
                    column.autoSizeColumn(),
                    column.columnWidth())
            );
            return null;
        };
    }

    public static Function<Pair<Subreport, Method>, Void> getSubreportsFunction(Map<Subreport, Method> subreportMap) {
        return pair -> {
            subreportMap.put(pair.getLeft(), pair.getRight());
            return null;
        };
    }

    public static boolean hasNestedTarget(Column column) {
        return !column.target().equals(StringUtils.EMPTY);
    }

    public static boolean hasNestedTarget(Cell cell) {
        return !cell.target().equals(StringUtils.EMPTY);
    }
}
