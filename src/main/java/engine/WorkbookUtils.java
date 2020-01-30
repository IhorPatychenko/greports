package engine;

import org.apache.poi.ss.usermodel.Cell;

import java.util.Date;
import java.util.Objects;

class WorkbookUtils {

    protected static void setCellValue(Cell cell, Object value) {
        if(value instanceof Date) {
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
