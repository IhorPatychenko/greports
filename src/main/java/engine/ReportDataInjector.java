package engine;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class ReportDataInjector {

    protected final XSSFWorkbook currentWorkbook;
    protected Map<String, XSSFCellStyle> _formatsCache = new HashMap<>();
    protected CreationHelper creationHelper;

    protected ReportDataInjector(XSSFWorkbook currentWorkbook) {
        this.currentWorkbook = currentWorkbook;
        this.creationHelper = this.currentWorkbook.getCreationHelper();
    }

    protected void setCellFormat(Cell cell, String format) {
        if(format != null && !format.isEmpty()){
            XSSFCellStyle cellStyle;
            if(!_formatsCache.containsKey(format)){
                cellStyle = currentWorkbook.createCellStyle();
                cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(format));
                _formatsCache.put(format, cellStyle);
            } else {
                cellStyle = _formatsCache.get(format);
            }
            cell.setCellStyle(cellStyle);
        }
    }

    protected void setCellValue(Cell cell, Object value) {
        if(value instanceof Date){
            cell.setCellValue(((Date) value));
        } else if(value instanceof Number){
            cell.setCellValue(((Number) value).doubleValue());
        } else if(value instanceof Boolean){
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(Objects.toString(value, ""));
        }
    }

    protected abstract void inject();
    protected abstract void injectData(Sheet sheet);

}
