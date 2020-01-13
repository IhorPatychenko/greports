package engine;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;

public class ReportLoaderError {

    private Integer rowIndex;
    private Integer columnIndex;
    private String cellReference;
    private String sheetName;
    private String rowReference;
    private String columnReference;
    private String errorMsg;

    public ReportLoaderError(Cell cell, String errorMsg){
        this.rowIndex = cell.getRowIndex();
        this.columnIndex = cell.getColumnIndex();
        final CellReference cellReference = new CellReference(cell);
        this.cellReference = cellReference.formatAsString();
        this.sheetName = cellReference.getCellRefParts()[0];
        this.rowReference = cellReference.getCellRefParts()[1];
        this.columnReference = cellReference.getCellRefParts()[2];
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public Integer getRowIndex(){
        return rowIndex;
    }

    public Integer getColumnIndex(){
        return columnIndex;
    }

    public String getCellReference() {
        return cellReference;
    }

    public String getSheetName(){
        return sheetName;
    }

    public String getRowReference(){
        return rowReference;
    }

    public String getColumnReference(){
        return columnReference;
    }
}
