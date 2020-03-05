package org.greports.content;

import org.greports.annotations.Configuration;
import org.greports.content.cell.AbstractReportCell;
import org.greports.content.cell.HeaderCell;
import org.greports.content.row.DataRow;
import org.greports.content.row.SpecialDataRow;
import org.greports.content.row.ReportRow;
import org.greports.exceptions.ReportEngineRuntimeException;
import org.greports.styles.ReportDataStyles;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReportData implements Cloneable {

    private final String reportName;
    private Configuration configuration;
    private String sheetName;
    private final URL templateURL;
    private ReportHeader header;
    private boolean createHeader;
    private int dataStartRow;
    private List<SpecialDataRow> specialRows = new ArrayList<>();
    private List<DataRow> dataRows = new ArrayList<>();
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

    public List<DataRow> getDataRows() {
        return dataRows;
    }

    public DataRow getDataRow(int index) {
        return dataRows.get(index);
    }

    public ReportRow getPhysicalRow(int rowIndex) {
        List<ReportRow> rows = new ArrayList<>();
        rows.add(header);
        rows.addAll(dataRows);
        rows.addAll(specialRows);
        final List<ReportRow> sorted = rows.stream().sorted(Comparator.comparing(ReportRow::getRowIndex)).collect(Collectors.toList());
        if(sorted.size() > rowIndex){
            return sorted.get(rowIndex);
        }
        throw new ReportEngineRuntimeException(String.format("Not existing row with rowIndex %d", rowIndex), this.getClass());
    }

    public int getRowsCount(){
        return dataRows.size();
    }

    public ReportHeader setHeader(ReportHeader header) {
        this.header = header;
        return this.header;
    }

    public ReportHeader getHeader() {
        return header;
    }

    public void addRow(DataRow row) {
        this.dataRows.add(row);
    }

    public void setCreateHeader(boolean createHeader) {
        this.createHeader = createHeader;
    }

    public boolean isCreateHeader() {
        return createHeader;
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
        int mergedCount = 0;
        for (int i = 0; header != null && i < header.getCells().size(); i++) {
            final HeaderCell headerCell = header.getCells().get(i);
            if(headerCell.isAutoSizeColumn()){
                autosizedColumns.add(i + mergedCount);
            }
            if(headerCell.getColumnWidth() > 1){
                mergedCount += headerCell.getColumnWidth() - 1;
            }
        }
        return autosizedColumns;
    }

    public ReportDataStyles getStyles() {
        return reportDataStyles;
    }

    public List<SpecialDataRow> getSpecialRows() {
        return specialRows;
    }

    public void addSpecialRow(SpecialDataRow specialDataRow) {
        specialRows.add(specialDataRow);
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

        header.getCells().sort(Comparator.comparing(AbstractReportCell::getPosition));
        dataRows.forEach(row -> row.getCells().sort(Comparator.comparing(AbstractReportCell::getPosition)));

        for (int i = 0; i < header.getCells().size(); i++) {
            HeaderCell headerCell = header.getCell(i);
            if(!headerCell.getId().equals("")){
                targetIndexes.put(headerCell.getId(), i);
            }
        }
    }

    private void mergeHeaders(ReportData other) {
        header.addCells(other.getHeader().getCells());
    }

    private void mergeRows(ReportData other) {
        for (int i = 0; i < dataRows.size(); i++) {
            getDataRow(i).addCells(other.getDataRow(i).getCells());
        }
    }

    private void mergeStyles(ReportData other) {
        reportDataStyles.mergeStyles(other.reportDataStyles);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        final ReportData clone = (ReportData) super.clone();
        clone.header = (ReportHeader) this.header.clone();
        clone.dataRows = dataRows.stream().map(row -> (DataRow) row.clone()).collect(Collectors.toList());
        clone.specialRows = specialRows.stream().map(row -> (SpecialDataRow) row.clone()).collect(Collectors.toList());
        return clone;
    }
}
