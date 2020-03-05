package org.greports.content.cell;

import org.greports.engine.ValueType;

public class DataCell extends AbstractReportCell {

    private final boolean physicalPosition;
    private final String format;
    private final ValueType valueType;
    private final int columnWidth;

    public DataCell(final Float position, final boolean physicalPosition, final String format, final Object value, final ValueType valueType) {
        this(position, physicalPosition, format, value, valueType, 1);
    }

    public DataCell(final Float position, final boolean physicalPosition, final String format, final Object value, final ValueType valueType, final int columnWidth) {
        super(position, value);
        this.physicalPosition = physicalPosition;
        this.format = format;
        this.valueType = valueType;
        this.columnWidth = columnWidth;
    }

    public boolean isPhysicalPosition() {
        return physicalPosition;
    }

    public String getFormat() {
        return format;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public int getColumnWidth() {
        return columnWidth;
    }
}
