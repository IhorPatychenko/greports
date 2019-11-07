import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class ReportData {
    private final String name;
    private ReportHeader header;
    private Collection<ReportDataRow> rows = new ArrayList<>();

    ReportData(@NotNull String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    public Collection<ReportDataRow> getRows() {
        return rows;
    }

    void orderColumns(){
        this.rows.forEach(ReportDataRow::orderColumns);
    }

    ReportHeader setHeader(ReportHeader header) {
        this.header = header;
        return this.header;
    }

    public ReportHeader getHeader() {
        return header;
    }

    void addRow(ReportDataRow row) {
        this.rows.add(row);
    }
}
