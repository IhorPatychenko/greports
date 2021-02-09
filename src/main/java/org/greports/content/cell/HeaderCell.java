package org.greports.content.cell;

import org.greports.engine.ReportSpecialColumn;

public class HeaderCell extends AbstractReportCell implements PositionedCell {
    private final String id;
    private final boolean autoSizeColumn;
    private final int columnWidth;
    private final Float position;

    public HeaderCell(Float position, String title, String id, boolean autoSizeColumn) {
        this(position, title, id, autoSizeColumn, 1);
    }

    public HeaderCell(Float position, String title, String id, boolean autoSizeColumn, int columnWidth) {
        this(position, title, "@", id, autoSizeColumn, columnWidth);
    }

    public HeaderCell(ReportSpecialColumn specialColumn, String idPrefix) {
        this(
            specialColumn.getPosition(),
            idPrefix,
            specialColumn.getId(),
            specialColumn.isAutoSizeColumn(),
            specialColumn.getColumnWidth()
        );
    }

    public HeaderCell(Float position, String title, String format, String id, boolean autoSizeColumn, int columnWidth) {
        super(title, format);
        this.position = position;
        this.id = id;
        this.autoSizeColumn = autoSizeColumn;
        this.columnWidth = columnWidth;
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

    @Override
    public Float getPosition() {
        return this.position;
    }
}
