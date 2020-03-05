package org.greports.content.row;

import org.greports.content.cell.ReportCell;
import org.greports.content.cell.SpecialDataCell;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpecialDataRow implements ReportRow, Cloneable {
    private int rowIndex;
    private List<SpecialDataCell> specialCells = new ArrayList<>();

    public SpecialDataRow(int rowIndex){
        this.rowIndex = rowIndex;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public List<SpecialDataCell> getSpecialCells() {
        return specialCells;
    }

    public void addCell(SpecialDataCell cell) {
        this.specialCells.add(cell);
    }

    @Override
    public ReportCell getCell(final int index) {
        return null;
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
