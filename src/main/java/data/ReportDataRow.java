package data;

import cell.ReportDataColumn;

import java.util.ArrayList;
import java.util.Collection;

public class ReportDataRow {
    private Collection<ReportDataColumn> columns = new ArrayList<>();

    public ReportDataRow() {}

    public Collection<ReportDataColumn> getColumns() {
        return columns;
    }

    public void addColumn(ReportDataColumn column){
        this.columns.add(column);
    }

}
