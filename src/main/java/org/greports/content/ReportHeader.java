package org.greports.content;

import org.greports.content.cell.HeaderCell;
import org.greports.content.row.ReportRow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReportHeader implements ReportRow, Cloneable {

    private List<HeaderCell> cells = new ArrayList<>();
    private final boolean columnFilter;
    private final int rowIndex;

    public ReportHeader(final boolean addFilter, final int rowIndex) {
        this.columnFilter = addFilter;
        this.rowIndex = rowIndex;
    }

    public List<HeaderCell> getCells() {
        return cells;
    }

    public HeaderCell getCell(int index){
        return cells.get(index);
    }

    @Override
    public Integer getRowIndex() {
        return rowIndex;
    }

    public void addCell(HeaderCell cell) {
        this.cells.add(cell);
    }

    public void addCells(Collection<HeaderCell> cells) {
        this.cells.addAll(cells);
    }

    public boolean isColumnFilter() {
        return columnFilter;
    }

    @Override
    public Object clone() {
        ReportHeader clone = this;
        try {
            clone = (ReportHeader) super.clone();
            List<HeaderCell> newCells = new ArrayList<>();
            for (final HeaderCell cell : cells) {
                newCells.add((HeaderCell) cell.clone());
            }
            clone.cells = newCells;
        } catch (CloneNotSupportedException ignored) {}
        return clone;
    }
}
