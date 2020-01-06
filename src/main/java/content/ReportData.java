package content;

import content.cell.ReportCell;
import content.row.ReportDataRow;
import content.row.ReportDataSpecialRow;
import styles.ReportDataStyles;

import java.io.InputStream;
import java.util.*;

public class ReportData {

    private final String name;
    private String sheetName;
    private String templatePath;
    private ReportHeader header;
    private boolean showHeader = true;
    private int headerStartRow;
    private int dataStartRow;
    private List<Integer> autoSizedColumns = new ArrayList<>();
    private List<ReportDataSpecialRow> specialRows = new ArrayList<>();
    private List<ReportDataRow> rows = new ArrayList<>();
    private ReportDataStyles reportDataStyles = new ReportDataStyles();
    private Map<String, Integer> targetIndexes = new HashMap<>();

    public ReportData(String name, String sheetName, String templatePath) {
        this.name = name;
        this.sheetName = !sheetName.isEmpty() ? sheetName : null;
        this.templatePath = templatePath;
    }

    public String getName() {
        return name;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public boolean isReportWithTemplate(){
        return !Objects.equals(templatePath, "");
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

    public int getColumnsCount() {
        return header.getCells().size();
    }

    public List<Integer> getAutoSizedColumns() {
        return autoSizedColumns;
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

    public Integer getColumnIndexForTarget(String target) {
        return targetIndexes.get(target);
    }

    public void mergeReportData(List<ReportData> subreportsData) {
        for (ReportData other : subreportsData) {
            mergeHeaders(other);
            mergeRows(other);
            mergeStyles(other);
        }

        header.getCells().sort(Comparator.comparing(ReportCell::getPosition));
        rows.forEach(row -> row.getCells().sort(Comparator.comparing(ReportCell::getPosition)));

        for (int i = 0; i < header.getCells().size(); i++) {
            targetIndexes.put(header.getCell(i).getId(), i);
        }
    }

    private void mergeHeaders(ReportData other) {
        header.addCells(other.getHeader().getCells());
    }

    private void mergeRows(ReportData other) {
        for (int i = 0; i < rows.size(); i++) {
            getRow(i).addCells(other.getRow(i).getCells());
        }
    }

    private void mergeStyles(ReportData other) {
        reportDataStyles.mergeStyles(other.reportDataStyles);
    }
}
