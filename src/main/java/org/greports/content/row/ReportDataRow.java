package org.greports.content.row;

import org.greports.content.column.ReportDataCell;

import java.util.ArrayList;
import java.util.List;

public class ReportDataRow {

    private final List<ReportDataCell> cells = new ArrayList<>();
    private final Integer rowIndex;

    public ReportDataRow(final Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public List<ReportDataCell> getCells() {
        return cells;
    }

    public void addCell(ReportDataCell cell){
        this.cells.add(cell);
    }

    public void addCells(List<ReportDataCell> cells) {
        this.cells.addAll(cells);
    }

    public ReportDataCell getColumn(int index) {
        return this.cells.get(index);
    }

}
