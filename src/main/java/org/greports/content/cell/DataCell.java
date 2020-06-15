package org.greports.content.cell;

import org.greports.engine.ValueType;

/**
 * This class represents a cell which contains an input data.
 */
public class DataCell extends AbstractReportCell implements PositionedCell {

    private final Float position;
    private final boolean physicalPosition;
    private final ValueType valueType;
    private int columnWidth;

    public DataCell(final Float position, final boolean physicalPosition, final String format, final Object value, final ValueType valueType) {
        this(position, physicalPosition, format, value, valueType, 1);
    }

    public DataCell(final Float position, final boolean physicalPosition, final String format, final Object value, final ValueType valueType, final int columnWidth) {
        super(value, format);
        this.position = position;
        this.physicalPosition = physicalPosition;
        this.valueType = valueType;
        this.columnWidth = columnWidth;
    }

    @Override
    public Float getPosition() {
        return position;
    }

    public boolean isPhysicalPosition() {
        return physicalPosition;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
    }
}
