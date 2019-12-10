package content;

import content.row.ReportDataRow;
import content.row.ReportDataSpecialRow;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.poi.util.StringUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ReportData {

    private final String name;
    private String sheetName;
    private InputStream template;
    private ReportHeader header;
    private boolean showHeader = true;
    private int headerStartRow;
    private int dataStartRow;
    private int columnsLength;
    private List<ReportDataSpecialRow> specialRows = new ArrayList<>();
    private List<ReportDataRow> rows = new ArrayList<>();
    private ReportDataStyles reportDataStyles = new ReportDataStyles();

    public ReportData(String name, String sheetName) {
        this.name = name;
        this.sheetName = sheetName != null && !sheetName.isEmpty() ? sheetName : null;
    }

    public String getName() {
        return name;
    }

    public String getSheetName() {
        return sheetName;
    }

    public InputStream getTemplateInputStream() {
        return template;
    }

    public void setTemplateInputStream(InputStream template) {
        this.template = template;
    }

    public List<ReportDataRow> getRows() {
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

    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
    }

    public boolean isShowHeader() {
        return showHeader;
    }

    public int getHeaderStartRow() {
        return headerStartRow;
    }

    public void setHeaderStartRow(int headerStartRow) {
        this.headerStartRow = headerStartRow;
    }

    public int getDataStartRow() {
        return dataStartRow;
    }

    public void setDataStartRow(int dataStartRow) {
        this.dataStartRow = dataStartRow;
    }

    public int getColumnsLength() {
        return columnsLength;
    }

    public ReportData setColumnsLength(int columns) {
        this.columnsLength = columns;
        return this;
    }

    public ReportDataStyles getStyles() {
        return reportDataStyles;
    }

    public List<ReportDataSpecialRow> getSpecialRows() {
        return specialRows;
    }

    public void addSpecialRow(ReportDataSpecialRow reportDataSpecialRow) {
        specialRows.add(reportDataSpecialRow);
    }
}
