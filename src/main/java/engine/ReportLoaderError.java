package engine;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;

public class ReportLoaderError {

    private Cell cell;
    private CellReference cellReference;
    private String errorMsg;

    public ReportLoaderError(Cell cell, String errorMsg){
        this.cell = cell;
        this.cellReference = new CellReference(cell);
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public Integer getRowIndex(){
        return cell.getRowIndex();
    }

    public Integer getColumnIndex(){
        return cell.getColumnIndex();
    }

    public String getCellReference() {
        return cellReference.formatAsString();
    }

    public String getSheetName(){
        return cellReference.getCellRefParts()[0];
    }

    public String getRowReference(){
        return cellReference.getCellRefParts()[1];
    }

    public String getColumnReference(){
        return cellReference.getCellRefParts()[2];
    }
}
