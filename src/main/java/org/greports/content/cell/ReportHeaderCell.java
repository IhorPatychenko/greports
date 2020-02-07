package org.greports.content.cell;

public class ReportHeaderCell extends ReportCell {
    private final String id;
    private final boolean autoSizeColumn;

    public ReportHeaderCell(Float position, String title, String id, boolean autoSizeColumn) {
        super(position, title);
        this.id = id;
        this.autoSizeColumn = autoSizeColumn;
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
}
