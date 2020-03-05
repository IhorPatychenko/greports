package org.greports.content.row;

import org.greports.content.cell.ReportCell;

public interface ReportRow {
    ReportCell getCell(int index);
    Integer getRowIndex();
}
