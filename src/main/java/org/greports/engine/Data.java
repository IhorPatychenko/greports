package org.greports.engine;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.greports.content.cell.DataCell;
import org.greports.content.cell.HeaderCell;
import org.greports.content.cell.SpecialDataRowCell;
import org.greports.content.header.ReportHeader;
import org.greports.content.row.DataRow;
import org.greports.content.row.ReportRow;
import org.greports.content.row.SpecialDataRow;
import org.greports.styles.StylesContainer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Data implements Cloneable, Serializable {
    private static final long serialVersionUID = 7890759064532349923L;

    private final StylesContainer stylesContainer = new StylesContainer();
    private final Map<String, Integer> targetIndexes = new HashMap<>();
    private final String reportName;
    private Configuration configuration;
    private ReportHeader header;
    private int dataStartRow;
    private final List<Pair<Integer, Integer>> groupedRows = new ArrayList<>();
    private boolean groupedRowsDefaultCollapsed;
    private List<Pair<Integer, Integer>> groupedColumns = new ArrayList<>();
    private boolean groupedColumnsDefaultCollapsed;
    private List<SpecialDataRow> specialRows = new ArrayList<>();
    private List<DataRow> dataRows = new ArrayList<>();
    private List<Integer> autosizedColumns;

    public Data(final String reportName, final Configuration configuration) {
        this.reportName = reportName;
        this.configuration = configuration;
    }

    public boolean isCellExist(final int rowIndex, final int columnIndex) {
        ReportRow<?> physicalRow = getPhysicalRow(rowIndex);
        return physicalRow != null && physicalRow.getCells().size() > columnIndex;
    }

    public Data addRow(DataRow row) {
        this.dataRows.add(row);
        return this;
    }

    public Data setDataRows(List<DataRow> rows) {
        this.dataRows = rows;
        return this;
    }

    public boolean isCreateHeader() {
        return this.configuration.isCreateHeader();
    }

    public boolean getGroupedRowsDefaultCollapsed() {
        return groupedRowsDefaultCollapsed;
    }

    public Data setCreateHeader(boolean createHeader) {
        this.configuration.setCreateHeader(createHeader);
        return this;
    }

    public Data addGroupedRow(final Pair<Integer, Integer> groupedRows) {
        this.groupedRows.add(groupedRows);
        return this;
    }

    public List<Pair<Integer, Integer>> getGroupedRows() {
        return groupedRows;
    }

    public Data setGroupedRowsDefaultCollapsed(final boolean groupedRowsDefaultCollapsed) {
        this.groupedRowsDefaultCollapsed = groupedRowsDefaultCollapsed;
        return this;
    }

    public boolean isGroupedRowsDefaultCollapsed() {
        return groupedRowsDefaultCollapsed;
    }

    public boolean isGroupedColumnsDefaultCollapsed() {
        return groupedColumnsDefaultCollapsed;
    }

    public Data setGroupedColumnsDefaultCollapsed(final boolean groupedColumnsDefaultCollapsed) {
        this.groupedColumnsDefaultCollapsed = groupedColumnsDefaultCollapsed;
        return this;
    }

    public Data setGroupedColumns(final List<Pair<Integer, Integer>> groupedColumns) {
        this.groupedColumns = groupedColumns;
        return this;
    }

    public List<Pair<Integer, Integer>> getGroupedColumns() {
        return groupedColumns;
    }

    public Data addSpecialRow(SpecialDataRow specialDataRow) {
        specialRows.add(specialDataRow);
        return this;
    }

    public void mergeReportData(List<Data> subreportsData) {
        for (Data other : subreportsData) {
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
            if(!StringUtils.EMPTY.equals(headerCell.getId())){
                targetIndexes.put(headerCell.getId(), i);
            }
        }
    }

    public void setColumnIndexes() {
        for(int i = 0; i < header.getCells().size(); i++) {
            header.getCell(i).setColumnIndex(i);
        }

        for(DataRow dataRow : dataRows) {
            for(int i = 0; i < dataRow.getCells().size(); i++) {
                dataRow.getCell(i).setColumnIndex(i);
            }
        }

        for(SpecialDataRow specialRow : specialRows) {
            for(SpecialDataRowCell specialCell : specialRow.getCells()) {
                specialCell.setColumnIndex(targetIndexes.get(specialCell.getTargetId()));
            }
        }
    }

    private void sortData() {
        header.getCells().sort(Comparator.comparing(HeaderCell::getPosition));
        dataRows.forEach(row -> row.getCells().sort(Comparator.comparing(DataCell::getPosition)));
    }

    private void mergeHeaders(Data other) {
        header.addCells(other.getHeader().getCells());
    }

    private void mergeRows(Data other) {
        for (int i = 0; i < dataRows.size(); i++) {
            getDataRow(i).addCells(other.getDataRow(i).getCells());
        }
    }

    private void mergeStyles(Data other) {
        stylesContainer.mergeStyles(other.stylesContainer);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        final Data clone = (Data) super.clone();
        clone.header = (ReportHeader) this.header.clone();
        clone.configuration = (Configuration) this.configuration.clone();
        clone.dataRows = dataRows.stream().map(row -> (DataRow) row.clone()).collect(Collectors.toList());
        clone.specialRows = specialRows.stream().map(row -> (SpecialDataRow) row.clone()).collect(Collectors.toList());
        return clone;
    }

    public String getReportName() {
        return reportName;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getSheetName() {
        return !configuration.getSheetName().isEmpty() ? configuration.getSheetName() : null;
    }

    public Data setSheetName(final String sheetName) {
        this.configuration.setSheetName(sheetName);
        return this;
    }

    public List<DataRow> getDataRows() {
        return dataRows;
    }

    public DataRow getDataRow(final int index) {
        return dataRows.get(index);
    }

    public ReportRow<?> getPhysicalRow(final int rowIndex) {
        final List<ReportRow<?>> sorted = this.getReportRows().stream().sorted(Comparator.comparing(ReportRow::getRowIndex)).collect(Collectors.toList());
        if(sorted.size() > rowIndex){
            return sorted.get(rowIndex);
        }
        return null;
    }

    public List<ReportRow<?>> getReportRows() {
        List<ReportRow<?>> rows = new ArrayList<>();
        if(isCreateHeader()) {
            rows.add(header);
        }
        rows.addAll(dataRows);
        rows.addAll(specialRows);
        return rows;
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

    public int getDataRealStartRow() {
        return dataStartRow + configuration.getVerticalOffset();
    }

    public Data setDataStartRow(int dataStartRow) {
        this.dataStartRow = dataStartRow;
        return this;
    }

    public int getColumnsCount() {
        return header.getCells().size();
    }

    public List<Integer> getAutoSizedColumns() {
        List<Integer> compiledAutosizedColumns = new ArrayList<>();

        if(this.autosizedColumns != null) {
            return this.autosizedColumns;
        }

        int mergedCount = 0;
        for (int i = 0; header != null && i < header.getCells().size(); i++) {
            final HeaderCell headerCell = header.getCells().get(i);
            if(headerCell.isAutoSizeColumn()){
                compiledAutosizedColumns.add(i + mergedCount);
            }
            if(headerCell.getColumnWidth() > 1){
                mergedCount += headerCell.getColumnWidth() - 1;
            }
        }

        return compiledAutosizedColumns;
    }

    public StylesContainer getStyles() {
        return stylesContainer;
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

    public void applyConfigurator(final Configurator configurator) {
        if(configurator != null) {
            // Override sheet name
            if(configurator.getSheetName() != null) this.setSheetName(configurator.getSheetName());

            // Override titles
            for (final Map.Entry<Integer, String> entry : configurator.getOverriddenTitles().entrySet()) {
                this.header.getCell(entry.getKey()).setValue(entry.getValue());
            }

            // Remove columns
            final List<Integer> removedColumns = configurator.getRemovedColumns();
            final List<ReportRow<?>> reportRows = this.getReportRows();
            for(int i = 0; i < removedColumns.size(); i++) {
                for(ReportRow<?> reportRow : reportRows) {
                    reportRow.removeCell(removedColumns.get(i));
                }
            }

            // Autosized columns
            this.autosizedColumns = configurator.getAutosizedColumns();
        }
    }
}
