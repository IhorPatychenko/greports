package engine;

import annotations.Column;
import annotations.Report;
import annotations.Configuration;
import annotations.SpecialColumn;
import annotations.Subreport;
import annotations.CellValidator;
import exceptions.ReportEngineReflectionException;
import exceptions.ReportEngineValidationException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import positioning.TranslationsParser;
import utils.AnnotationUtils;
import utils.Pair;
import utils.Translator;
import validators.AbstractValidator;
import validators.ValidatorFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static exceptions.ReportEngineRuntimeExceptionCode.*;
import static exceptions.ReportEngineRuntimeExceptionCode.INSTANTIATION_ERROR;

public class ReportLoader {

    public enum ReportLoaderErrorTreatment {
        SKIP_ROW_ON_ERROR, SKIP_COLUMN_ON_ERROR, THROW_ERROR
    }

    private String reportName;
    private Workbook currentWorkbook;
    private ReportLoaderResult loaderResult;
    private Translator translator;

    public ReportLoader(String reportName, String filePath) throws IOException, InvalidFormatException {
        this(reportName, new File(filePath));
    }

    public ReportLoader(String reportName, File file) throws IOException, InvalidFormatException {
        this(reportName, new FileInputStream(file));
    }

    public ReportLoader(String reportName, InputStream inputStream) throws IOException, InvalidFormatException {
        this(reportName, WorkbookFactory.create(inputStream));
    }

    private ReportLoader(String reportName, Workbook workbook) {
        this.reportName = reportName;
        this.currentWorkbook = workbook;
        this.loaderResult = new ReportLoaderResult();
    }

    public <T> ReportLoader bindForClass(Class<T> clazz) throws ReportEngineReflectionException, IOException {
        return bindForClass(clazz, ReportLoaderErrorTreatment.THROW_ERROR);
    }

    public <T> ReportLoader bindForClass(Class<T> clazz, ReportLoaderErrorTreatment treatment) throws ReportEngineReflectionException, IOException {
        final Configuration configuration = getClassReportConfiguration(clazz);
        this.translator = new Translator(new TranslationsParser(configuration.translationsDir()).parse(configuration.reportLang()));
        final Map<Annotation, Pair<Class<?>, Method>> annotations = loadColumns(clazz, configuration, false);
        final Map<Annotation, Pair<Class<?>, Method>> unwindedAnnotations = loadColumns(clazz, configuration, true);
        List<T> bindForClass = bindForClass(clazz, configuration, annotations, unwindedAnnotations, treatment);
        loaderResult.addResult(clazz, bindForClass);
        return this;
    }

    private <T> List<T> bindForClass(Class<T> clazz, Configuration configuration, Map<Annotation, Pair<Class<?>, Method>> annotations, Map<Annotation, Pair<Class<?>, Method>> unwindedAnnotations, ReportLoaderErrorTreatment treatment) throws ReportEngineReflectionException {
        List<Pair<List<?>, Method>> subreportsData = new ArrayList<>();
        List<T> instances = new ArrayList<>();
        List<Annotation> keys = new ArrayList<>(unwindedAnnotations.keySet());
        boolean entryError = false;
        try {
            final Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            final Sheet sheet = currentWorkbook.getSheet(configuration.sheetName());
            for(int dataRowNum = configuration.dataStartRowIndex(); dataRowNum <= sheet.getLastRowNum() - AnnotationUtils.getLastSpecialRowsCount(configuration); dataRowNum++) {
                final Row row = sheet.getRow(dataRowNum);
                final T instance = declaredConstructor.newInstance();
                for (final Map.Entry<Annotation, Pair<Class<?>, Method>> entry : annotations.entrySet()) {
                    final Annotation annotation = entry.getKey();
                    final Pair<Class<?>, Method> pair = entry.getValue();
                    if(annotation instanceof Column){
                        final Column column = (Column) annotation;
                        final Method method = pair.getRight();
                        final Cell cell = row.getCell(keys.indexOf(annotation));
                        try {
                            instanceSetValueFromCell(method, instance, cell, column.cellValidators());
                        } catch (ReportEngineValidationException e) {
                            if(ReportLoaderErrorTreatment.THROW_ERROR.equals(treatment)){
                                throw new ReportEngineReflectionException(e.getMessage(), ILLEGAL_ARGUMENT);
                            } else {
                                loaderResult.addError(clazz, cell, e.getMessage(), column.title());
                                entryError = true;
                                if(ReportLoaderErrorTreatment.SKIP_ROW_ON_ERROR.equals(treatment)){
                                    break;
                                }
                            }
                        }
                    } else if(annotation instanceof Subreport){
                        final Class<?> subreportClass = pair.getLeft();
                        final Configuration subreportConfiguration = getClassReportConfiguration(subreportClass);
                        final Map<Annotation, Pair<Class<?>, Method>> subreportAnnotations = loadColumns(subreportClass, subreportConfiguration, false);
                        final List<?> list = bindForClass(subreportClass, subreportConfiguration, subreportAnnotations, unwindedAnnotations, treatment);
                        subreportsData.add(Pair.of(list, pair.getRight()));
                    }
                }

                if(!entryError || ReportLoaderErrorTreatment.SKIP_COLUMN_ON_ERROR.equals(treatment)){
                    instances.add(instance);

                    for (final Pair<List<?>, Method> pair : subreportsData) {
                        final Method method = pair.getRight();
                        final List<?> results = pair.getLeft();
                        for (int i = 0; i < results.size(); i++) {
                            final T t = instances.get(i);
                            final Object o = results.get(i);
                            method.invoke(t, o);
                        }
                    }
                }
                entryError = false;
            }
        } catch (NoSuchMethodException e) {
            throw new ReportEngineReflectionException("Error obtaining constructor reference" , NO_METHOD_ERROR);
        } catch (InstantiationException e) {
            throw new ReportEngineReflectionException("Error instantiating an object", INSTANTIATION_ERROR);
        } catch (IllegalAccessException e) {
            throw new ReportEngineReflectionException("Error executing method witch does not have access to the definition of the specified class", ILLEGAL_ACCESS);
        } catch (InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error executing method witch does not have access to the definition of the specified class", INVOCATION_ERROR);
        }
        return instances;
    }

    private Configuration getClassReportConfiguration(Class<?> clazz) {
        final Report reportAnnotation = AnnotationUtils.getReportAnnotation(clazz);
        return AnnotationUtils.getReportConfiguration(reportAnnotation, reportName);
    }

    private <T> Map<Annotation, Pair<Class<?>, Method>> loadColumns(Class<T> clazz, Configuration configuration, boolean recursive) {

        Map<Column, Pair<Class<?>, Method>> columnsMap = new LinkedHashMap<>();
        final Function<Pair<Column, Pair<Class<?>, Method>>, Void> columnsFunction = AnnotationUtils.getColumnsWithFieldAndMethodsFunction(columnsMap);
        AnnotationUtils.columnsWithMethodAnnotations(clazz, columnsFunction, reportName);
        Map<Annotation, Pair<Class<?>, Method>> annotations = new LinkedHashMap<>(columnsMap);

        Map<Subreport, Pair<Class<?>, Method>> subreportsMap = new LinkedHashMap<>();
        final Function<Pair<Subreport, Pair<Class<?>, Method>>, Void> subreportsFunction = AnnotationUtils.getSubreportsWithFieldsAndMethodsFunction(subreportsMap);
        AnnotationUtils.subreportsWithFieldsAndMethodAnnotations(clazz, subreportsFunction, reportName);

        final SpecialColumn[] specialColumns = configuration.specialColumns();

        if(recursive) {
            for (Map.Entry<Subreport, Pair<Class<?>, Method>> entry : subreportsMap.entrySet()) {
                final Map<Annotation, Pair<Class<?>, Method>> map = loadColumns(entry.getValue().getLeft(), getClassReportConfiguration(entry.getValue().getLeft()), true);
                annotations.putAll(map);
            }
        } else {
            annotations.putAll(subreportsMap);
        }

        for (SpecialColumn specialColumn : specialColumns) {
            annotations.put(specialColumn, Pair.of(null, null));
        }

        if(recursive){
            annotations = sortAnnotationsByPosition(annotations);
        }
        return annotations;
    }

    private static Map<Annotation, Pair<Class<?>, Method>> sortAnnotationsByPosition(Map<Annotation, Pair<Class<?>, Method>> map) {
        List<Map.Entry<Annotation, Pair<Class<?>, Method>>> list = new LinkedList<>(map.entrySet());
        list.sort((o1, o2) -> {
            final Annotation key1 = o1.getKey(), key2 = o2.getKey();
            Float value1, value2;
            if(key1 instanceof Column){
                value1 = ((Column) key1).position();
            } else {
                value1 = ((SpecialColumn) key1).position();
            }
            if(key2 instanceof Column){
                value2 = ((Column) key2).position();
            } else {
                value2 = ((SpecialColumn) key2).position();
            }
            return value1.compareTo(value2);
        });

        Map<Annotation, Pair<Class<?>, Method>> result = new LinkedHashMap<>();
        for (Map.Entry<Annotation, Pair<Class<?>, Method>> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private void instanceSetValueFromCell(final Method method, final Object instance, final Cell cell, final CellValidator[] cellValidators) throws ReportEngineReflectionException, ReportEngineValidationException {
        method.setAccessible(true);
        Class<?> parameterType = method.getParameterTypes()[0];
        Object value = null;
        try {
            if(cell != null){
                if(CellType.BOOLEAN.equals(cell.getCellTypeEnum())){
                    value = cell.getBooleanCellValue();
                } else if(CellType.STRING.equals(cell.getCellTypeEnum())){
                    value = cell.getRichStringCellValue().getString();
                } else if(CellType.NUMERIC.equals(cell.getCellTypeEnum())){
                    if (DateUtil.isCellDateFormatted(cell)) {
                        value = cell.getDateCellValue();
                    } else if(parameterType.equals(Double.class) || parameterType.getName().equals("double")){
                        value = cell.getNumericCellValue();
                    } else if(parameterType.equals(Integer.class) || parameterType.getName().equals("int")) {
                        value = new Double(cell.getNumericCellValue()).intValue();
                    } else if(parameterType.equals(Long.class) || parameterType.getName().equals("long")){
                        value = new Double(cell.getNumericCellValue()).longValue();
                    } else if(parameterType.equals(Float.class) || parameterType.getName().equals("float")){
                        value = new Double(cell.getNumericCellValue()).floatValue();
                    } else if(parameterType.equals(Short.class) || parameterType.getName().equals("short")){
                        value = new Double(cell.getNumericCellValue()).shortValue();
                    }
                } else if(CellType.FORMULA.equals(cell.getCellTypeEnum())) {
                    value = cell.getCellFormula();
                }
                checkValidations(value, cellValidators);
                method.invoke(instance, value);
            }
        } catch (IllegalAccessException e) {
            throw new ReportEngineReflectionException("Error executing method witch does not have access to the definition of the specified class", ILLEGAL_ACCESS);
        } catch (InvocationTargetException e) {
            throw new ReportEngineReflectionException("Error executing method witch does not have access to the definition of the specified class", INVOCATION_ERROR);
        }
    }

    private void checkValidations(final Object value, final CellValidator[] cellValidators) throws ReportEngineValidationException, ReportEngineReflectionException {
        for (final CellValidator cellValidator : cellValidators) {
            try {
                AbstractValidator validatorInstance = ValidatorFactory.get(cellValidator.validatorClass(), cellValidator.value());
                validate(validatorInstance, value, cellValidator.errorMessage());
            } catch (ReflectiveOperationException e) {
                throw new ReportEngineValidationException("Error instantiating a validator @" + cellValidator.validatorClass().getSimpleName(), INSTANTIATION_ERROR);
            }
        }
    }

    private void validate(final AbstractValidator validatorInstance, final Object value, final String errorMessageKey) throws ReportEngineValidationException {
        if(!validatorInstance.isValid(value)){
            String errorMessage = translator.translate(errorMessageKey, validatorInstance.getValidatorValue());
//            String errorMessage = translations.getOrDefault(errorMessageKey, errorMessageKey).toString();
//            if(validatorInstance.getValidatorValue() != null){
//                errorMessage = errorMessage.replace("%value%", validatorInstance.getValidatorValue());
//            }
            throw new ReportEngineValidationException(errorMessage.replace("%value%", errorMessage), VALIDATION_ERROR);
        }
    }

    public ReportLoaderResult getLoaderResult() {
        return loaderResult;
    }
}
