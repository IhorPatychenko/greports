package org.greports.content.row;

import org.greports.content.cell.SpecialDataCell;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpecialDataRow implements ReportRow<SpecialDataCell>, Cloneable {

    private int rowIndex;
    private boolean stickyRow;
    private List<SpecialDataCell> specialCells = new ArrayList<>();

    public SpecialDataRow(int rowIndex, boolean stickyRow){
        this.rowIndex = rowIndex;
        this.stickyRow = stickyRow;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public void addCell(SpecialDataCell cell) {
        specialCells.add(cell);
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public boolean isStickyRow() {
        return stickyRow;
    }

    @Override
    public SpecialDataCell getCell(final int index) {
        return specialCells.get(index);
    }

    @Override
    public List<SpecialDataCell> getCells() {
        return specialCells;
    }

    @Override
    public Object clone() {
        SpecialDataRow clone = this;
        try {
            clone = (SpecialDataRow) super.clone();
            clone.specialCells = specialCells.stream().map(cell -> (SpecialDataCell) cell.clone()).collect(Collectors.toList());
        } catch (CloneNotSupportedException ignored) {}
        return clone;
    }
}
