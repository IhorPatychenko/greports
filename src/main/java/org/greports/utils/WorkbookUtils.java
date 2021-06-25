package org.greports.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.greports.engine.ValueType;
import org.greports.exceptions.GreportsRuntimeException;

import java.util.Date;
import java.util.Objects;

public class WorkbookUtils {

    private WorkbookUtils() {}

    public static void setCellValue(Cell cell, Object value) {
        setCellValue(cell, value, ValueType.PLAIN_VALUE);
    }

    public static void setCellValue(Cell cell, Object value, ValueType valueType) {
        if(ValueType.FORMULA.equals(valueType)) {
            cell.setCellFormula(value.toString());
        } else if(value instanceof Date) {
            cell.setCellValue(((Date) value));
        } else if(value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if(value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(Objects.toString(value, StringUtils.EMPTY));
        }
    }

    public static Sheet getOrCreateSheet(Workbook workbook, String sheetName) {
        if(sheetName == null) {
            return workbook.createSheet();
        } else {
            final Sheet sheet = workbook.getSheet(sheetName);
            return sheet != null ? sheet : workbook.createSheet(sheetName);
        }
    }

    public static Row getOrCreateRow(Sheet sheet, Integer rowIndex) {
        final Row row = sheet.getRow(rowIndex);
        return row != null ? row : sheet.createRow(rowIndex);
    }

    public static Cell getOrCreateCell(Row row, Integer cellIndex) {
        final Cell cell = row.getCell(cellIndex);
        return cell != null ? cell : row.createCell(cellIndex);
    }

    public static int getLastRowNum(Workbook workbook, String sheetName) {
        final Sheet sheet = workbook.getSheet(sheetName);
        if(sheet == null) {
            throw new GreportsRuntimeException(String.format("The sheet with name %s does not exist", sheetName), WorkbookUtils.class);
        }
        return getLastRowNum(sheet);
    }

    public static int getLastRowNum(Sheet sheet) {
        return sheet.getLastRowNum();
    }
}
