package engine;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;

public class ReportLoaderError {

    private Cell cell;
    private CellReference cellReference;
    private Class<?> valueClass;
    private Class<?> methodParameterClass;

    public ReportLoaderError(Cell cell, Class<?> valueClass, Class<?> methodParameterClass) {
        this.cell = cell;
        this.cellReference = new CellReference(cell);
        this.valueClass = valueClass;
        this.methodParameterClass = methodParameterClass;
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

    public Class<?> getValueClass() {
        return valueClass;
    }

    public String getValueClassName(){
        return valueClass.getSimpleName();
    }

    public Class<?> getMethodParameterClass() {
        return methodParameterClass;
    }

    public String getMethodParameterClassName(){
        return methodParameterClass.getSimpleName();
    }
}
