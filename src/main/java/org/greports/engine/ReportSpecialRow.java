package org.greports.engine;

import org.greports.annotations.SpecialRow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReportSpecialRow implements Cloneable, Serializable {
    private static final long serialVersionUID = -110378709759679479L;

    private int rowIndex;
    private List<ReportSpecialRowCell> cells = new ArrayList<>();

    ReportSpecialRow(SpecialRow specialRow) {
        this.rowIndex = specialRow.rowIndex();
        this.cells = Arrays.stream(specialRow.cells()).map(ReportSpecialRowCell::new).collect(Collectors.toList());
    }

    public ReportSpecialRow(final int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public ReportSpecialRow setRowIndex(final int rowIndex) {
        this.rowIndex = rowIndex;
        return this;
    }

    public List<ReportSpecialRowCell> getCells() {
        return cells;
    }

    public ReportSpecialRow setCells(final List<ReportSpecialRowCell> cells) {
        this.cells = cells;
        return this;
    }

    @Override
    public Object clone() {
        ReportSpecialRow clone = this;
        try {
            clone = (ReportSpecialRow) super.clone();
            clone.cells = cells.stream().map(cell -> (ReportSpecialRowCell) cell.clone()).collect(Collectors.toList());
        } catch (CloneNotSupportedException ignored) {}
        return clone;
    }
}
