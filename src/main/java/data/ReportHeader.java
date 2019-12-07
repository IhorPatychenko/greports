package data;

import cell.ReportHeaderCell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReportHeader {

    private List<ReportHeaderCell> cells = new ArrayList<>();

    public List<ReportHeaderCell> getCells() {
        return cells;
    }

    public ReportHeaderCell getCell(int index){
        return cells.get(index);
    }

    public void addCell(ReportHeaderCell cell) {
        this.cells.add(cell);
    }

    public ReportHeader addCells(Collection<ReportHeaderCell> cells) {
        this.cells.addAll(cells);
        return this;
    }

}
