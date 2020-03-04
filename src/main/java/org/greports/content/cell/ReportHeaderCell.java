package org.greports.content.cell;

public class ReportHeaderCell extends ReportCell {
    private final String id;
    private final boolean autoSizeColumn;
    private final int columnWidth;

    public ReportHeaderCell(Float position, String title, String id, boolean autoSizeColumn) {
        this(position, title, id, autoSizeColumn, 1);
    }

    public ReportHeaderCell(Float position, String title, String id, boolean autoSizeColumn, int columnWidth) {
        super(position, title);
        this.id = id;
        this.autoSizeColumn = autoSizeColumn;
        this.columnWidth = columnWidth;
    }

    public void setTitle(String newTitle){
        super.setTitle(newTitle);
    }

    public String getId() {
        return id;
    }

    public boolean isAutoSizeColumn() {
        return autoSizeColumn;
    }

    public int getColumnWidth() {
        return columnWidth;
    }
}
