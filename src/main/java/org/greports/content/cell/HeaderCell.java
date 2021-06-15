package org.greports.content.cell;

import lombok.Getter;
import org.greports.engine.SpecialColumn;
import org.greports.engine.ValueType;
import org.greports.utils.CellFormats;

@Getter
public class HeaderCell extends AbstractReportCell implements PositionedCell {
    private final String id;
    private final boolean autoSizeColumn;
    private final int columnWidth;
    private final Float position;

    /**
     * @param position relative cell position
     * @param title cell title
     * @param id cell id
     * @param autoSizeColumn {@code true} if the column should be autosized
     */
    public HeaderCell(Float position, String title, String id, boolean autoSizeColumn) {
        this(position, title, id, autoSizeColumn, 1);
    }

    /**
     * @param position relative cell position
     * @param title cell title
     * @param id cell id
     * @param autoSizeColumn {@code true} if the column should be autosized
     * @param columnWidth column width
     */
    public HeaderCell(Float position, String title, String id, boolean autoSizeColumn, int columnWidth) {
        super(title, CellFormats.TEXT, ValueType.PLAIN_VALUE);
        this.position = position;
        this.id = id;
        this.autoSizeColumn = autoSizeColumn;
        this.columnWidth = columnWidth;
    }

    /**
     * A constructor which creates a header from a special column
     *
     * @param specialColumn special column
     * @param idPrefix id prefix
     */
    public HeaderCell(SpecialColumn specialColumn, String idPrefix) {
        this(
            specialColumn.getPosition(),
            idPrefix,
            specialColumn.getId(),
            specialColumn.isAutoSizeColumn(),
            specialColumn.getColumnWidth()
        );
    }
}
