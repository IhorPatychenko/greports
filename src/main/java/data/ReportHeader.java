package data;

import cell.ReportHeaderCell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReportHeader {

    private List<ReportHeaderCell> cells = new ArrayList<>();
    private boolean columnFilter;

    public ReportHeader(){
        this(false);
    }

    public ReportHeader(boolean addFilter) {
        this.columnFilter = addFilter;
    }

    public List<ReportHeaderCell> getCells() {
        return cells;
    }

    public ReportHeaderCell getCell(int index){
        return cells.get(index);
    }

    public void addCell(ReportHeaderCell cell) {
        this.cells.add(cell);
    }

    public void addCells(Collection<ReportHeaderCell> cells) {
        this.cells.addAll(cells);
    }

    public boolean isColumnFilter() {
        return columnFilter;
    }
}
