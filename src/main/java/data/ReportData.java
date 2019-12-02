package data;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

public class ReportData {
    private final String name;
    private InputStream template;
    private ReportHeader header;
    private Collection<ReportDataRow> rows = new ArrayList<>();

    public ReportData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public InputStream getTemplateInputStream() {
        return template;
    }

    public void setTemplateInputStream(InputStream template) {
        this.template = template;
    }

    public Collection<ReportDataRow> getRows() {
        return rows;
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
