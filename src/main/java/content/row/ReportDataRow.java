package content.row;

import content.column.ReportDataCell;

import java.util.ArrayList;
import java.util.List;

public class ReportDataRow {

    private List<ReportDataCell> columns = new ArrayList<>();

    public ReportDataRow() {}

    public List<ReportDataCell> getColumns() {
        return columns;
    }

    public void addCell(ReportDataCell column){
        this.columns.add(column);
    }

    public ReportDataCell getColumn(int index) {
        return this.columns.get(index);
    }

}
