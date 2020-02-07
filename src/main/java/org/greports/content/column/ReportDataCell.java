package org.greports.content.column;

import org.greports.content.cell.ReportCell;
import org.greports.engine.ValueType;

public class ReportDataCell extends ReportCell {

    private final Object value;
    private final String format;
    private final ValueType valueType;

    public ReportDataCell(Float position, String format, Object value, ValueType valueType) {
        super(position, null);
        this.format = format;
        this.value = value;
        this.valueType = valueType;
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

}
