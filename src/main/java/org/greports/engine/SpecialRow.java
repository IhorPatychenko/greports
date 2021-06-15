package org.greports.engine;

import lombok.Getter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SpecialRow implements Cloneable, Serializable {
    private static final long serialVersionUID = -110378709759679479L;

    private int rowIndex;
    private boolean stickyRow = false;
    private List<SpecialRowCell> cells = new ArrayList<>();

    SpecialRow(org.greports.annotations.SpecialRow specialRow) {
        this.rowIndex = specialRow.rowIndex();
        this.stickyRow = specialRow.stickyRow();
        this.cells = Arrays.stream(specialRow.cells()).map(SpecialRowCell::new).collect(Collectors.toList());
    }

    public SpecialRow(final int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public SpecialRow setRowIndex(final int rowIndex) {
        this.rowIndex = rowIndex;
        return this;
    }

    public SpecialRow setStickyRow(boolean stickyRow) {
        this.stickyRow = stickyRow;
        return this;
    }

    public SpecialRow setCells(final List<SpecialRowCell> cells) {
        this.cells = cells;
        return this;
    }

    @Override
    public Object clone() {
        SpecialRow clone = this;
        try {
            clone = (SpecialRow) super.clone();
            clone.cells = cells.stream().map(cell -> (SpecialRowCell) cell.clone()).collect(Collectors.toList());
        } catch (CloneNotSupportedException ignored) {}
        return clone;
    }
}
