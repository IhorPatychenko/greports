package org.greports.engine;

import org.apache.poi.ss.usermodel.Cell;

import java.util.Date;
import java.util.Objects;

class WorkbookUtils {

    protected static void setCellValue(Cell cell, Object value) {
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
            cell.setCellValue(Objects.toString(value, ""));
        }
    }
}
