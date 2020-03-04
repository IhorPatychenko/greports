package org.greports.content.cell;

import org.greports.engine.ValueType;

public class ReportDataSpecialRowCell {
    private final ValueType valueType;
    private final Object value;
    private final String format;
    private final String targetId;
    private final int columnWidth;

    public ReportDataSpecialRowCell(final ValueType valueType, final Object value, final String format, final String targetId) {
        this(valueType, value, format, targetId, 1);
    }

    public ReportDataSpecialRowCell(final ValueType valueType, final Object value, final String format, final String targetId, final int columnWidth) {
        this.valueType = valueType;
        this.value = value;
        this.format = format;
        this.targetId = targetId;
        this.columnWidth = columnWidth;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public Object getValue() {
        return value;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getFormat() {
        return format;
    }

    public int getColumnWidth() {
        return columnWidth;
    }
}
