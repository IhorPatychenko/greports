package engine;

import content.ReportData;
import exceptions.ReportEngineInjectorException;
import exceptions.ReportEngineRuntimeException;
import exceptions.ReportEngineRuntimeExceptionCode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.HashMap;
import java.util.Map;

public abstract class ReportDataInjector {

    protected final XSSFWorkbook currentWorkbook;
    protected final ReportData reportData;
    protected Map<String, XSSFCellStyle> _formatsCache = new HashMap<>();
    protected final CreationHelper creationHelper;

    protected ReportDataInjector(XSSFWorkbook currentWorkbook, ReportData reportData) {
        this.currentWorkbook = currentWorkbook;
        this.reportData = reportData;
        this.creationHelper = this.currentWorkbook.getCreationHelper();
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
        final Cell cell = row.getCell(reportData.getColumnIndexForTarget(id));
        if(cell == null){
            throw new ReportEngineInjectorException(
                String.format("Error occurred trying to obtain the cell for row \"%d\" and id \"%s\"", row.getRowNum(), id),
                ReportEngineRuntimeExceptionCode.INJECTOR_ERROR
            );
        }
        return new CellReference(cell);
    }

    protected abstract void inject();
    protected abstract void injectData(Sheet sheet);

}
