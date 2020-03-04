package org.greports.content.column;

import org.greports.content.cell.ReportCell;
import org.greports.engine.ValueType;

public class ReportDataCell extends ReportCell {

    private final boolean physicalPosition;
    private final Object value;
    private final String format;
    private final ValueType valueType;
    private final int columnWidth;

    public ReportDataCell(final Float position, final boolean physicalPosition, final String format, final Object value, final ValueType valueType) {
        this(position, physicalPosition, format, value, valueType, 1);
    }

    public ReportDataCell(final Float position, final boolean physicalPosition, final String format, final Object value, final ValueType valueType, final int columnWidth) {
        super(position, null);
        this.physicalPosition = physicalPosition;
        this.format = format;
        this.value = value;
        this.valueType = valueType;
        this.columnWidth = columnWidth;
    }

    public boolean isPhysicalPosition() {
        return physicalPosition;
    }

    public Object getValue() {
        return value;
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
