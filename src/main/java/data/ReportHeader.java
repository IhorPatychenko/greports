package data;

import cell.ReportHeaderCell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ReportHeader {
    private Collection<ReportHeaderCell> cells = new ArrayList<>();

    public Collection<ReportHeaderCell> getCells() {
        return cells;
    }

    public void addCell(ReportHeaderCell cell) {
        this.cells.add(cell);
    }

    public ReportHeader addCells(Collection<ReportHeaderCell> cells) {
        this.cells.addAll(cells);
        return this;
    }

    public ReportHeader sortCells(){
        this.cells = cells.stream().sorted(Comparator.comparing(ReportHeaderCell::getPosition)).collect(Collectors.toList());
        return this;
    }
}
