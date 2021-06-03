package org.greports.content.cell;

import lombok.Getter;
import org.greports.engine.ValueType;

/**
 * This class represents a cell which contains an input data.
 */
@Getter
public class DataCell extends AbstractReportCell implements PositionedCell {

    private final Float position;
    private final boolean physicalPosition;
    private int columnWidth;

    /**
     * @param position cell position
     * @param physicalPosition {@code true} if cell position is not relative, but absolute
     * @param format cell format
     * @param value cell value
     * @param valueType cell value type
     *
     * @see ValueType
     */
    public DataCell(final Float position, final boolean physicalPosition, final String format, final Object value, final ValueType valueType) {
        this(position, physicalPosition, format, value, valueType, 1);
    }

    /**
     * @param position cell position
     * @param physicalPosition {@code true} if cell position is not relative, but absolute
     * @param format cell format
     * @param value cell value
     * @param valueType cell value type
     * @param columnWidth column width
     *
     * @see ValueType
     */
    public DataCell(final Float position, final boolean physicalPosition, final String format, final Object value, final ValueType valueType, final int columnWidth) {
        super(value, format, valueType);
        this.position = position;
        this.physicalPosition = physicalPosition;
        this.columnWidth = columnWidth;
    }

    /**
     * @param columnWidth new column width
     */
    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
    }
}
