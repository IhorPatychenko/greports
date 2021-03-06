package org.greports.content.row;

import org.greports.content.cell.ReportCell;

import java.util.List;

public interface ReportRow<T extends ReportCell> {
    T getCell(int index);
    List<T> getCells();
    Integer getRowIndex();

    default void removeCell(int i) {
        List<T> cells = this.getCells();
        cells.removeIf(cell -> cell.getColumnIndex() == i);
    }
}
