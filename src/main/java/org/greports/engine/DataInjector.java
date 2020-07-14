package org.greports.engine;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greports.services.LoggerService;

import java.util.HashMap;
import java.util.Map;

public abstract class DataInjector {

    protected final XSSFWorkbook currentWorkbook;
    protected final ReportData reportData;
    protected final CreationHelper creationHelper;
    protected LoggerService loggerService;
    protected Map<String, XSSFCellStyle> formatsCache = new HashMap<>();

    protected abstract void inject();

    protected abstract void injectData(Sheet sheet);

    protected DataInjector(XSSFWorkbook currentWorkbook, ReportData reportData, boolean loggerEnabled) {
        this.currentWorkbook = currentWorkbook;
        this.reportData = reportData;
        this.creationHelper = this.currentWorkbook.getCreationHelper();
        this.loggerService = new LoggerService(this.getClass(), loggerEnabled);
    }

    protected CellReference getCellReferenceForTargetId(Row row, String id) {
        return new CellReference(row.getCell(reportData.getColumnIndexForId(id)));
    }

    protected void setCellFormat(Cell cell, String format) {
        if(format != null && !format.isEmpty()){
            XSSFCellStyle cellStyle;
            if(!formatsCache.containsKey(format)){
                cellStyle = currentWorkbook.createCellStyle();
                cellStyle.cloneStyleFrom(cell.getCellStyle());
                cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(format));
                formatsCache.put(format, cellStyle);
            } else {
                cellStyle = formatsCache.get(format);
            }
            cell.setCellStyle(cellStyle);
        }
    }

    protected String replaceFormulaIndexes(Row targetRow, String value) {
        for (Map.Entry<String, Integer> entry : reportData.getTargetIndexes().entrySet()) {
            value = value.replaceAll(entry.getKey(), this.getCellReferenceForTargetId(targetRow, entry.getKey()).formatAsString());
        }
        return value;
    }

    protected void adjustColumns(Sheet sheet) {
        for (Integer autoSizedColumn : reportData.getAutoSizedColumns()) {
            sheet.autoSizeColumn(autoSizedColumn);
        }
    }

}
