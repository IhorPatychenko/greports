package content;

import content.cell.ReportCell;
import content.cell.ReportHeaderCell;
import content.row.ReportDataRow;
import content.row.ReportDataSpecialRow;
import styles.ReportDataStyles;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReportData {

    private final String name;
    private String sheetName;
    private URL templateURL;
    private ReportHeader header;
    private boolean createHeader = true;
    private int headerStartRow;
    private int dataStartRow;
    private List<Integer> autoSizedColumns = new ArrayList<>();
    private List<ReportDataSpecialRow> specialRows = new ArrayList<>();
    private List<ReportDataRow> rows = new ArrayList<>();
    private ReportDataStyles reportDataStyles = new ReportDataStyles();
    private Map<String, Integer> targetIndexes = new HashMap<>();

    public ReportData(String name, String sheetName, URL templateURL) {
        this.name = name;
        this.sheetName = !sheetName.isEmpty() ? sheetName : null;
        this.templateURL = templateURL;
    }

    public String getName() {
        return name;
    }

    public String getSheetName() {
        return sheetName;
    }

    public URL getTemplateURL() {
        return templateURL;
    }

    public boolean isReportWithTemplate(){
        return !Objects.equals(templateURL, null);
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

    public void setCreateHeader(boolean createHeader) {
        this.createHeader = createHeader;
    }

    public boolean isCreateHeader() {
        return createHeader;
    }

    public int getHeaderRowIndex() {
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

    public Map<String, Integer> getTargetIndexes() {
        return targetIndexes;
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
            ReportHeaderCell headerCell = header.getCell(i);
            if(!headerCell.getId().equals("")){
                targetIndexes.put(headerCell.getId(), i);
            }
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
