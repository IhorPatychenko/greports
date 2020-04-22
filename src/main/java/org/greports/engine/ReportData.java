package org.greports.engine;

import org.greports.annotations.Configuration;
import org.greports.content.ReportHeader;
import org.greports.content.cell.AbstractReportCell;
import org.greports.content.cell.HeaderCell;
import org.greports.content.row.DataRow;
import org.greports.content.row.ReportRow;
import org.greports.content.row.SpecialDataRow;
import org.greports.styles.ReportDataStyles;
import org.greports.utils.Pair;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReportData implements Cloneable, Serializable {
    private static final long serialVersionUID = 7890759064532349923L;

    private final ReportDataStyles reportDataStyles = new ReportDataStyles();
    private final Map<String, Integer> targetIndexes = new HashMap<>();
    private String reportName;
    private ReportConfiguration configuration;
    private URL templateURL;
    private ReportHeader header;
    private boolean createHeader;
    private int dataStartRow;
    private List<Pair<Integer, Integer>> groupedRows = new ArrayList<>();
    private boolean groupedRowsDefaultCollapsed;
    private List<Pair<Integer, Integer>> groupedColumns = new ArrayList<>();
    private boolean groupedColumnsDefaultCollapsed;
    private List<SpecialDataRow> specialRows = new ArrayList<>();
    private List<DataRow> dataRows = new ArrayList<>();

    public ReportData(final ReportConfiguration configuration) {
        this.configuration = configuration;
    }

    ReportData(final String reportName, final Configuration configuration, final URL templateURL) {
        this.reportName = reportName;
        this.configuration = new ReportConfiguration(configuration);
        this.templateURL = templateURL;
    }

    public boolean isReportWithTemplate(){
        return !Objects.equals(templateURL, null);
    }

    public boolean isCellExist(final int rowIndex, final int columnIndex) {
        ReportRow<?> physicalRow = getPhysicalRow(rowIndex);
        return physicalRow != null && physicalRow.getCells().size() > columnIndex;
    }

    public ReportData addRow(DataRow row) {
        this.dataRows.add(row);
        return this;
    }

    public ReportData setDataRows(List<DataRow> rows) {
        this.dataRows = rows;
        return this;
    }

    public boolean isCreateHeader() {
        return createHeader;
    }

    public boolean getGroupedRowsDefaultCollapsed() {
        return groupedRowsDefaultCollapsed;
    }

    public ReportData setCreateHeader(boolean createHeader) {
        this.createHeader = createHeader;
        return this;
    }

    public ReportData addGroupedRow(final Pair<Integer, Integer> groupedRows) {
        this.groupedRows.add(groupedRows);
        return this;
    }

    public List<Pair<Integer, Integer>> getGroupedRows() {
        return groupedRows;
    }

    public ReportData setGroupedRowsDefaultCollapsed(final boolean groupedRowsDefaultCollapsed) {
        this.groupedRowsDefaultCollapsed = groupedRowsDefaultCollapsed;
        return this;
    }

    public boolean isGroupedRowsDefaultCollapsed() {
        return groupedRowsDefaultCollapsed;
    }

    public boolean isGroupedColumnsDefaultCollapsed() {
        return groupedColumnsDefaultCollapsed;
    }

    public ReportData setGroupedColumnsDefaultCollapsed(final boolean groupedColumnsDefaultCollapsed) {
        this.groupedColumnsDefaultCollapsed = groupedColumnsDefaultCollapsed;
        return this;
    }

    public ReportData setGroupedColumns(final List<Pair<Integer, Integer>> groupedColumns) {
        this.groupedColumns = groupedColumns;
        return this;
    }

    public List<Pair<Integer, Integer>> getGroupedColumns() {
        return groupedColumns;
    }

    public ReportData addSpecialRow(SpecialDataRow specialDataRow) {
        specialRows.add(specialDataRow);
        return this;
    }

    public void mergeReportData(List<ReportData> subreportsData) {
        for (ReportData other : subreportsData) {
            mergeHeaders(other);
            mergeRows(other);
            mergeStyles(other);
        }

        setTargetIds();
    }

    public void setTargetIds() {
        sortData();

        for (int i = 0; i < header.getCells().size(); i++) {
            HeaderCell headerCell = header.getCell(i);
            if(!headerCell.getId().equals("")){
                targetIndexes.put(headerCell.getId(), i);
            }
        }
    }

    private void sortData() {
        header.getCells().sort(Comparator.comparing(AbstractReportCell::getPosition));
        dataRows.forEach(row -> row.getCells().sort(Comparator.comparing(AbstractReportCell::getPosition)));
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
        clone.configuration = (ReportConfiguration) this.configuration.clone();
        clone.dataRows = dataRows.stream().map(row -> (DataRow) row.clone()).collect(Collectors.toList());
        clone.specialRows = specialRows.stream().map(row -> (SpecialDataRow) row.clone()).collect(Collectors.toList());
        return clone;
    }

    public String getReportName() {
        return reportName;
    }

    public ReportConfiguration getConfiguration() {
        return configuration;
    }

    public String getSheetName() {
        return !configuration.getSheetName().isEmpty() ? configuration.getSheetName() : null;
    }

    public ReportData setSheetName(final String sheetName) {
        this.configuration.setSheetName(sheetName);
        return this;
    }

    public URL getTemplateURL() {
        return templateURL;
    }

    public List<DataRow> getDataRows() {
        return dataRows;
    }

    public DataRow getDataRow(final int index) {
        return dataRows.get(index);
    }

    public ReportRow<?> getPhysicalRow(final int rowIndex) {
        List<ReportRow<?>> rows = new ArrayList<>();
        if(createHeader) {
            rows.add(header);
        }
        rows.addAll(dataRows);
        rows.addAll(specialRows);
        final List<ReportRow<?>> sorted = rows.stream().sorted(Comparator.comparing(ReportRow::getRowIndex)).collect(Collectors.toList());
        if(sorted.size() > rowIndex){
            return sorted.get(rowIndex);
        }
        return null;
    }

    public int getRowsCount(){
        return dataRows.size();
    }

    public ReportHeader getHeader() {
        return header;
    }

    public int getDataStartRow() {
        return dataStartRow;
    }

    public ReportData setDataStartRow(int dataStartRow) {
        this.dataStartRow = dataStartRow;
        return this;
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

    public Integer getColumnIndexForId(String target) {
        return targetIndexes.get(target);
    }

    public Map<String, Integer> getTargetIndexes() {
        return targetIndexes;
    }

    public ReportHeader setHeader(ReportHeader header) {
        this.header = header;
        return this.header;
    }

    public void applyConfigurator(final ReportConfigurator configurator) {
        // Override sheet name
        if(configurator.getSheetName() != null) this.setSheetName(configurator.getSheetName());

        // Override titles
        for (final Map.Entry<Integer, String> entry : configurator.getOverriddenTitles().entrySet()) {
            this.getHeader().getCell(entry.getKey()).setValue(entry.getValue());
        }

        // Override template URL
        if(configurator.getTemplateUrl() != null) this.templateURL = configurator.getTemplateUrl();
    }
}
