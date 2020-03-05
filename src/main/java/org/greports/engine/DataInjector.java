package org.greports.engine;

import org.greports.content.ReportData;
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

    protected LoggerService loggerService;

    protected final XSSFWorkbook currentWorkbook;
    protected final ReportData reportData;
    protected Map<String, XSSFCellStyle> _formatsCache = new HashMap<>();
    protected final CreationHelper creationHelper;

    protected DataInjector(XSSFWorkbook currentWorkbook, ReportData reportData, boolean loggerEnabled) {
        this.currentWorkbook = currentWorkbook;
        this.reportData = reportData;
        this.creationHelper = this.currentWorkbook.getCreationHelper();
        this.loggerService = new LoggerService(this.getClass(), loggerEnabled);
    }

    protected void setCellFormat(Cell cell, String format) {
        if(format != null && !format.isEmpty()){
            XSSFCellStyle cellStyle;
            if(!_formatsCache.containsKey(format)){
                cellStyle = currentWorkbook.createCellStyle();
                cellStyle.cloneStyleFrom(cell.getCellStyle());
                cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(format));
                _formatsCache.put(format, cellStyle);
            } else {
                cellStyle = _formatsCache.get(format);
            }
            cell.setCellStyle(cellStyle);
        }
    }

    protected CellReference getCellReferenceForTargetId(Row row, String id) {
        return new CellReference(row.getCell(reportData.getColumnIndexForTarget(id)));
    }

    protected abstract void inject();
    protected abstract void injectData(Sheet sheet);

}
