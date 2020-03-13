package org.greports.engine;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;

import java.io.Serializable;

public class ReportLoaderError implements Serializable {

    private static final long serialVersionUID = 2763041428905940945L;

    private final Integer rowIndex;
    private final Integer columnIndex;
    private String cellReference;
    private final String sheetName;
    private String rowReference;
    private String columnReference;
    private final String columnTitle;
    private final String errorMsg;
    private final transient Serializable errorValue;

    public ReportLoaderError(String sheetName, Integer rowIndex, Integer columnIndex, String columnTitle, String errorMsg, Serializable errorValue) {
        this.sheetName = sheetName;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.columnTitle = columnTitle;
        this.errorMsg = errorMsg;
        this.errorValue = errorValue;
    }

    public ReportLoaderError(Cell cell, String columnTitle, String errorMsg, final Serializable errorValue) {
        this(cell.getSheet().getSheetName(), cell.getRowIndex(), cell.getColumnIndex(), columnTitle, errorMsg, errorValue);
        final CellReference cellReference = new CellReference(cell);
        this.cellReference = cellReference.formatAsString();
        this.rowReference = cellReference.getCellRefParts()[1];
        this.columnReference = cellReference.getCellRefParts()[2];
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public Integer getColumnIndex() {
        return columnIndex;
    }

    public String getCellReference() {
        return cellReference;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getRowReference() {
        return rowReference;
    }

    public String getColumnReference() {
        return columnReference;
    }

    public String getColumnTitle() {
        return columnTitle;
    }

    public Object getErrorValue() {
        return errorValue;
    }
}
