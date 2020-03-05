package org.greports.content.cell;

public class HeaderCell extends AbstractReportCell {
    private final String id;
    private final boolean autoSizeColumn;
    private final int columnWidth;

    public HeaderCell(Float position, String title, String id, boolean autoSizeColumn) {
        this(position, title, id, autoSizeColumn, 1);
    }

    public HeaderCell(Float position, String title, String id, boolean autoSizeColumn, int columnWidth) {
        super(position, title);
        this.id = id;
        this.autoSizeColumn = autoSizeColumn;
        this.columnWidth = columnWidth;
    }

    public void setValue(String newValue){
        super.setValue(newValue);
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
