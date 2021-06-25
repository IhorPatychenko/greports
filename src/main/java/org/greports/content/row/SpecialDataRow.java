package org.greports.content.row;

import org.greports.content.cell.SpecialDataRowCell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SpecialDataRow implements ReportRow<SpecialDataRowCell>, Cloneable {

    private int rowIndex;
    private final boolean stickyRow;
    private List<SpecialDataRowCell> cells = new ArrayList<>();

    public SpecialDataRow(org.greports.annotations.SpecialRow specialRow) {
        this.rowIndex = specialRow.rowIndex();
        this.stickyRow = specialRow.stickyRow();
    }

    public SpecialDataRow(int rowIndex, boolean stickyRow){
        this.rowIndex = rowIndex;
        this.stickyRow = stickyRow;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public void addCell(SpecialDataRowCell cell) {
        cells.add(cell);
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public boolean isStickyRow() {
        return stickyRow;
    }

    @Override
    public SpecialDataRowCell getCell(final int index) {
        return cells.get(index);
    }

    @Override
    public List<SpecialDataRowCell> getCells() {
        return cells;
    }

    @Override
    public Object clone() {
        SpecialDataRow clone = this;
        try {
            clone = (SpecialDataRow) super.clone();
            clone.cells = cells.stream().map(cell -> (SpecialDataRowCell) cell.clone()).collect(Collectors.toList());
        } catch (CloneNotSupportedException ignored) {}
        return clone;
    }
}
