package data;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class ReportData {
    private final String name;
    private ReportHeader header;
    private Collection<ReportDataRow> rows = new ArrayList<>();

    public ReportData(@NotNull String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Collection<ReportDataRow> getRows() {
        return rows;
    }

    public void orderColumns(){
        this.rows.forEach(ReportDataRow::orderColumns);
    }

    public ReportHeader setHeader(ReportHeader header) {
        this.header = header;
        return this.header;
    }

    public ReportHeader getHeader() {
        return header;
    }

    public void addRow(ReportDataRow row) {
        this.rows.add(row);
    }
}
