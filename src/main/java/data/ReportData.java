package data;

import styles.PositionedStyle;
import styles.HorizontalRangedStyle;
import styles.RectangleRangedStyle;
import styles.ReportStylesBuilder;
import styles.VerticalRangedStyle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ReportData {

    private final String name;
    private InputStream template;
    private ReportHeader header;
    private boolean showHeader = true;
    private int headerStartRow;
    private int dataStartRow;
    private int columnsLength;
    private List<ReportDataRow> rows = new ArrayList<>();
    private ReportStylesBuilder<VerticalRangedStyle> rowStyles;
    private ReportStylesBuilder<HorizontalRangedStyle> columnStyles;
    private ReportStylesBuilder<PositionedStyle> positionedStyles;
    private ReportStylesBuilder<RectangleRangedStyle> rangedStyleReportStyles;

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

    public ReportStylesBuilder<VerticalRangedStyle> getRowStyles() {
        return rowStyles;
    }

    public void setRowStyles(ReportStylesBuilder<VerticalRangedStyle> rowStyles) {
        this.rowStyles = rowStyles;
    }

    public ReportStylesBuilder<HorizontalRangedStyle> getColumnStyles() {
        return columnStyles;
    }

    public void setColumnStyles(ReportStylesBuilder<HorizontalRangedStyle> columnStyles) {
        this.columnStyles = columnStyles;
    }

    public ReportStylesBuilder<PositionedStyle> getPositionedStyles() {
        return positionedStyles;
    }

    public void setPositionedStyles(ReportStylesBuilder<PositionedStyle> positionedStyles) {
        this.positionedStyles = positionedStyles;
    }

    public ReportStylesBuilder<RectangleRangedStyle> getRangedStyleReportStyles() {
        return rangedStyleReportStyles;
    }

    public ReportData setRangedStyleReportStyles(ReportStylesBuilder<RectangleRangedStyle> rangedStyleReportStyles) {
        this.rangedStyleReportStyles = rangedStyleReportStyles;
        return this;
    }
}
