package org.greports.utils;

import org.greports.annotations.Cell;
import org.greports.annotations.CellGetter;
import org.greports.annotations.CellValidator;
import org.greports.annotations.Column;
import org.greports.annotations.ColumnGetter;
import org.greports.annotations.ColumnSetter;
import org.greports.annotations.ColumnValidator;
import org.greports.annotations.Configuration;
import org.greports.annotations.Converter;
import org.greports.annotations.Report;
import org.greports.annotations.SpecialColumn;
import org.greports.annotations.Subreport;
import org.greports.annotations.SubreportGetter;
import org.greports.annotations.SubreportSetter;
import org.greports.content.cell.HeaderCell;
import org.greports.engine.ReportBlock;
import org.greports.engine.ValueType;
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

public class AnnotationUtils {

    private static Report getReportAnnotation(Class<?> clazz) {
        final Report annotation = clazz.getAnnotation(Report.class);
        if (annotation != null) {
            return annotation;
        }
        throw new ReportEngineRuntimeException(clazz.toString() + " is not annotated as @Report", clazz);
    }

    public static Configuration getReportConfiguration(Class<?> clazz, String reportName) {
        final Report report = getReportAnnotation(clazz);
        return Arrays.stream(report.reportConfigurations())
            .filter(entry -> Arrays.asList(entry.reportName()).contains(reportName))
            .findFirst()
            .orElseThrow(() -> new ReportEngineRuntimeException(
                String.format("@Report has no @Configuration annotation with name \"%s\"", reportName),
                report.getClass()
            ));
    }

    public static int getLastSpecialRowsCount(Configuration configuration) {
        return (int) Arrays.stream(configuration.specialRows())
                .filter(entry -> Integer.MAX_VALUE == entry.rowIndex()).count();
    }

    public static <T> void methodsWithColumnAnnotations(Class<T> clazz, Function<Pair<Method, Column>, Void> columnFunction, String reportName) throws ReportEngineReflectionException {
        for (Field declaredField : getAllClassFields(clazz)) {
            final Column[] columns = declaredField.getAnnotationsByType(Column.class);
            for (Column column : columns) {
                if (getReportColumnPredicate(reportName).test(column)) {
                    final Method method = ReflectionUtils.fetchFieldGetter(declaredField, clazz);
                    columnFunction.apply(Pair.of(method, column));
                }
            }
        }

        for (final Method declaredMethod : getAllClassMethods(clazz)) {
            final ColumnGetter[] columnGetters = declaredMethod.getAnnotationsByType(ColumnGetter.class);
            for (final ColumnGetter columnGetter : columnGetters) {
                final Column column = getColumnAnnotationFromColumnGetter(columnGetter);
                if (getReportColumnPredicate(reportName).test(column)) {
                    columnFunction.apply(Pair.of(declaredMethod, column));
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
                final Cell cell = getCellFromCellGetter(cellGetter);
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
                final Column column = getColumnAnnotationFromColumnGetter(columnGetter);
                if (getReportColumnPredicate(reportName).test(column)) {
                    list.add(column);
                }
            }
        }
        return list.stream().max(Comparator.comparing(Column::position)).orElse(null);
    }

    public static <T> Map<Annotation, Method> loadBlockAnnotations(final ReportBlock reportBlock) throws ReportEngineReflectionException {
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
                final Subreport subreport = getSubreportFromSubreportSetter(optionalSubreport.get());
                map.put(subreport, method);
            }

            final Optional<ColumnSetter> optionalColumn = Arrays.stream(method.getAnnotationsByType(ColumnSetter.class))
                    .filter(column -> Arrays.asList(column.reportName()).contains(reportBlock.getReportName()))
                    .findFirst();
            if(optionalColumn.isPresent()) {
                final Column column = getColumnAnnotationFromColumnSetter(optionalColumn.get());
                map.put(column, method);
            }
        }

        Configuration configuration = getReportConfiguration(clazz, reportBlock.getReportName());
        for (SpecialColumn specialColumn : configuration.specialColumns()) {
            map.put(specialColumn, null);
        }
        return map;
    }

    public static <T> void methodsWithSubreportAnnotations(Class<T> clazz, Function<Pair<Method, Subreport>, Void> columnFunction, String reportName) throws ReportEngineReflectionException {
        for (Field field : getAllClassFields(clazz)) {
            final Subreport[] subreports = field.getAnnotationsByType(Subreport.class);
            for (Subreport subreport : subreports) {
                if (getSubreportPredicate(reportName).test(subreport)) {
                    final Method method = ReflectionUtils.fetchFieldGetter(field, clazz);
                    columnFunction.apply(Pair.of(method, subreport));
                }
            }
        }

        for (final Method method : getAllClassMethods(clazz)) {
            final SubreportGetter[] subreportGetters = method.getAnnotationsByType(SubreportGetter.class);
            for (final SubreportGetter subreportGetter : subreportGetters) {
                final Subreport subreport = getSubreportFromSubreportGetter(subreportGetter);
                if (getSubreportPredicate(reportName).test(subreport)) {
                    columnFunction.apply(Pair.of(method, subreport));
                }
            }
        }
    }

    private static Subreport getSubreportFromSubreportGetter(final SubreportGetter subreportGetter) {
        return new Subreport() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Subreport.class;
            }

            @Override
            public String[] reportName() {
                return subreportGetter.reportName();
            }

            @Override
            public float position() {
                return subreportGetter.position();
            }

            @Override
            public String id() {
                return subreportGetter.id();
            }
        };
    }

    private static Subreport getSubreportFromSubreportSetter(final SubreportSetter subreportGetter) {
        return new Subreport() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Subreport.class;
            }

            @Override
            public String[] reportName() {
                return subreportGetter.reportName();
            }

            @Override
            public float position() {
                return subreportGetter.position();
            }

            @Override
            public String id() {
                return subreportGetter.id();
            }
        };
    }

    private static Column getColumnAnnotationFromColumnGetter(final ColumnGetter columnGetter) {
        return new Column() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Column.class;
            }

            @Override
            public String[] reportName() {
                return columnGetter.reportName();
            }

            @Override
            public float position() {
                return columnGetter.position();
            }

            @Override
            public CellValidator[] cellValidators() {
                return new CellValidator[0];
            }

            @Override
            public ColumnValidator[] columnValidators() {
                return new ColumnValidator[0];
            }

            @Override
            public Converter[] getterConverter() {
                return columnGetter.typeConverter();
            }

            @Override
            public Converter[] setterConverters() {
                return new Converter[0];
            }

            @Override
            public String title() {
                return columnGetter.title();
            }

            @Override
            public String format() {
                return columnGetter.format();
            }

            @Override
            public ValueType valueType() {
                return columnGetter.valueType();
            }

            @Override
            public String id() {
                return columnGetter.id();
            }

            @Override
            public boolean autoSizeColumn() {
                return columnGetter.autoSizeColumn();
            }

            @Override
            public int columnWidth() {
                return columnGetter.columnWidth();
            }
        };
    }


    private static Cell getCellFromCellGetter(final CellGetter cellGetter) {
        return new Cell() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Cell.class;
            }

            @Override
            public String[] reportName() {
                return cellGetter.reportName();
            }

            @Override
            public int row() {
                return cellGetter.row();
            }

            @Override
            public int column() {
                return cellGetter.column();
            }

            @Override
            public CellValidator[] cellValidators() {
                return cellGetter.cellValidators();
            }

            @Override
            public Converter[] getterConverter() {
                return cellGetter.getterConverter();
            }

            @Override
            public Converter[] setterConverters() {
                return new Converter[0];
            }

            @Override
            public String format() {
                return cellGetter.format();
            }

            @Override
            public ValueType valueType() {
                return cellGetter.valueType();
            }

            @Override
            public String id() {
                return cellGetter.id();
            }

            @Override
            public boolean autoSizeColumn() {
                return cellGetter.autoSizeColumn();
            }

            @Override
            public int columnWidth() {
                return cellGetter.columnWidth();
            }
        };
    }

    private static Column getColumnAnnotationFromColumnSetter(final ColumnSetter columnSetter) {
        return new Column() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Column.class;
            }

            @Override
            public String[] reportName() {
                return columnSetter.reportName();
            }

            @Override
            public float position() {
                return columnSetter.position();
            }

            @Override
            public CellValidator[] cellValidators() {
                return columnSetter.cellValidators();
            }

            @Override
            public ColumnValidator[] columnValidators() {
                return columnSetter.columnValidators();
            }

            @Override
            public Converter[] getterConverter() {
                return new Converter[0];
            }

            @Override
            public Converter[] setterConverters() {
                return columnSetter.typeConverters();
            }

            @Override
            public String title() {
                return columnSetter.title();
            }

            @Override
            public String format() {
                return columnSetter.format();
            }

            @Override
            public ValueType valueType() {
                return columnSetter.valueType();
            }

            @Override
            public String id() {
                return columnSetter.id();
            }

            @Override
            public boolean autoSizeColumn() {
                return columnSetter.autoSizeColumn();
            }

            @Override
            public int columnWidth() {
                return columnSetter.columnWidth();
            }
        };
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

    public static Function<Pair<Method, Column>, Void> getMethodsAndColumnsFunction(Map<Method, Column> columnsMap) {
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

    public static Function<Pair<Method, Column>, Void> getHeadersFunction(List<HeaderCell> cells, Translator translator, Float positionIncrement, String idPrefix) {
        return pair -> {
            Column column = pair.getRight();
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

    public static Function<Pair<Method, Subreport>, Void> getSubreportsFunction(Map<Method, Subreport> subreportMap) {
        return pair -> {
            subreportMap.put(pair.getLeft(), pair.getRight());
            return null;
        };
    }
}
