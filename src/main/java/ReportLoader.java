import annotations.Report;
import annotations.ReportColumn;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import utils.AnnotationUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class ReportLoader {

    private String reportName;
    private Workbook workbook;

    public ReportLoader(String reportName, File file) throws IOException, InvalidFormatException {
        this(reportName, new FileInputStream(file));
    }

    public ReportLoader(String reportName, InputStream inputStream) throws IOException, InvalidFormatException {
        this.reportName = reportName;
        this.workbook = WorkbookFactory.create(inputStream);
    }

    public <T> List<T> bindForClass(Class<T> clazz) throws Exception {
        final Report reportAnnotation = AnnotationUtils.getReportAnnotation(reportName, clazz);
        AnnotationUtils.checkReportAnnotation(reportAnnotation, clazz, reportName);
        final List<AbstractMap.SimpleEntry<Method, ReportColumn>> simpleEntries = loadMethodColumns(clazz);
        return this.loadData(reportAnnotation, simpleEntries, clazz);
    }

    private <T> List<T> loadData(Report reportAnnotation, List<AbstractMap.SimpleEntry<Method, ReportColumn>> simpleEntries, Class<T> clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        List<T> data = new ArrayList<>();
        Sheet sheet = workbook.getSheet(reportAnnotation.sheetName());
        for(int i = reportAnnotation.dataOffset(); i <= sheet.getLastRowNum(); i++) {
            final T instance = clazz.newInstance();
            final Row row = sheet.getRow(i);
            for(int cellIndex = row.getFirstCellNum(), methodIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++, methodIndex++) {
                final Cell cell = row.getCell(cellIndex);
                final Method method = simpleEntries.get(methodIndex).getKey();
                instanceSetCellValue(method, instance, cell);
            }
            data.add(instance);
        }
        return data;
    }

    private <T> void instanceSetCellValue(final Method method, final T instance, final Cell cell) throws InvocationTargetException, IllegalAccessException {
        if(CellType.BOOLEAN.equals(cell.getCellTypeEnum())){
            method.invoke(instance, cell.getBooleanCellValue());
        } else if(CellType.STRING.equals(cell.getCellTypeEnum())){
            method.invoke(instance, cell.getRichStringCellValue().getString());
        } else if(CellType.NUMERIC.equals(cell.getCellTypeEnum())){
            if (DateUtil.isCellDateFormatted(cell)) {
                method.invoke(instance, cell.getDateCellValue());
            } else {
                final Class<?> parameterType = method.getParameterTypes()[0];
                if(parameterType.equals(Double.class)){
                    method.invoke(instance, cell.getNumericCellValue());
                } else if(parameterType.equals(Integer.class)) {
                    method.invoke(instance, new Double(cell.getNumericCellValue()).intValue());
                } else if(parameterType.equals(Long.class)){
                    method.invoke(instance, new Double(cell.getNumericCellValue()).longValue());
                } else if(parameterType.equals(Float.class)){
                    method.invoke(instance, new Double(cell.getNumericCellValue()).floatValue());
                } else if(parameterType.equals(Short.class)){
                    method.invoke(instance, new Double(cell.getNumericCellValue()).shortValue());
                }
            }
        } else if(CellType.FORMULA.equals(cell.getCellTypeEnum())) {
            method.invoke(instance, cell.getCellFormula());
        } else {
            method.invoke(instance, "");
        }
    }

    private <T> List<AbstractMap.SimpleEntry<Method, ReportColumn>> loadMethodColumns(Class<T> clazz){
        List<AbstractMap.SimpleEntry<Method, ReportColumn>> list = new ArrayList<>();
        Function<AbstractMap.SimpleEntry<Method, ReportColumn>, Void> columnFunction = entry -> {
            list.add(entry);
            return null;
        };
        AnnotationUtils.loadMethodsColumns(clazz, columnFunction, reportName, true);
        list.sort(Comparator.comparing(a -> a.getValue().position()));
        return list;
    }

}
