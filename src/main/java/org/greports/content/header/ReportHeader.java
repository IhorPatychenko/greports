package org.greports.content.header;

import org.greports.content.cell.HeaderCell;
import org.greports.content.row.ReportRow;
import org.greports.engine.ReportConfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReportHeader implements ReportRow<HeaderCell>, Cloneable, Serializable {

    private static final long serialVersionUID = -927334852923873496L;
    private List<HeaderCell> cells = new ArrayList<>();
    private final boolean columnFilter;
    private final boolean stickyHeader;
    private final int rowIndex;

    public ReportHeader(final boolean addFilter, final boolean stickyHeader, final int rowIndex) {
        this.columnFilter = addFilter;
        this.stickyHeader = stickyHeader;
        this.rowIndex = rowIndex;
    }

    public ReportHeader(ReportConfiguration configuration) {
        this(configuration.isSortableHeader(), configuration.isStickyHeader(), configuration.getHeaderRowIndex());
    }

    public void addCell(HeaderCell cell) {
        this.cells.add(cell);
    }

    public ReportHeader addCells(List<HeaderCell> cells) {
        this.cells.addAll(cells);
        return this;
    }

    public boolean isColumnFilter() {
        return columnFilter;
    }

    public boolean isStickyHeader() {
        return stickyHeader;
    }

    @Override
    public List<HeaderCell> getCells() {
        return cells;
    }

    @Override
    public HeaderCell getCell(int index){
        return cells.get(index);
    }

    @Override
    public Integer getRowIndex() {
        return rowIndex;
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
