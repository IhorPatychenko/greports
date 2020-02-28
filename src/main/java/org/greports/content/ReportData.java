package org.greports.content;

import org.greports.annotations.Configuration;
import org.greports.content.cell.ReportCell;
import org.greports.content.cell.ReportHeaderCell;
import org.greports.content.row.ReportDataRow;
import org.greports.content.row.ReportDataSpecialRow;
import org.greports.styles.ReportDataStyles;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReportData {

    private final String reportName;
    private Configuration configuration;
    private String sheetName;
    private final URL templateURL;
    private ReportHeader header;
    private boolean createHeader;
    private int headerStartRow;
    private int dataStartRow;
    private final List<ReportDataSpecialRow> specialRows = new ArrayList<>();
    private final List<ReportDataRow> rows = new ArrayList<>();
    private final ReportDataStyles reportDataStyles = new ReportDataStyles();
    private final Map<String, Integer> targetIndexes = new HashMap<>();

    public ReportData(String reportName, Configuration configuration, URL templateURL) {
        this.reportName = reportName;
        this.configuration = configuration;
        this.sheetName = !configuration.sheetName().isEmpty() ? configuration.sheetName() : null;
        this.templateURL = templateURL;
    }

    public String getReportName() {
        return reportName;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(final String sheetName) {
        this.sheetName = sheetName;
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
        List<Integer> autosizedColumns = new ArrayList<>();
        for (int i = 0; header != null && i < header.getCells().size(); i++) {
            if(header.getCells().get(i).isAutoSizeColumn()){
                autosizedColumns.add(i);
            }
        }
        return autosizedColumns;
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
