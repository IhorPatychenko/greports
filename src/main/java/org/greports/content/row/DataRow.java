package org.greports.content.row;

import org.greports.content.cell.DataCell;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataRow implements ReportRow<DataCell>, Cloneable {

    private List<DataCell> cells = new ArrayList<>();
    private final Integer rowIndex;

    public DataRow(final Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void addCell(DataCell cell){
        this.cells.add(cell);
    }

    public void addCells(List<DataCell> cells) {
        this.cells.addAll(cells);
    }

    public DataCell getCell(int index) {
        return this.cells.get(index);
    }

    @Override
    public List<DataCell> getCells() {
        return cells;
    }

    @Override
    public Object clone() {
        DataRow clone = this;
        try {
            clone = (DataRow) super.clone();
            clone.cells = cells.stream().map(cell -> (DataCell) cell.clone()).collect(Collectors.toList());
            return clone;
        } catch (CloneNotSupportedException ignored) {}
        return clone;
    }
}
