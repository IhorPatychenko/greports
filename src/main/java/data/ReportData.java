package data;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReportData {
    private final String name;
    private InputStream template;
    private ReportHeader header;
    private List<ReportDataRow> rows = new ArrayList<>();

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

    public Iterable<ReportDataRow> getRows() {
        return rows;
    }

    public ReportDataRow getRow(int index) {
        return rows.get(index);
    }

    public int getRowsCount(){
        return rows.size();
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
