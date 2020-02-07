package org.greports.content.cell;

import org.greports.engine.ValueType;

public class ReportDataSpecialRowCell {
    private final ValueType valueType;
    private final Object value;
    private final String format;
    private final String targetId;

    public ReportDataSpecialRowCell(final ValueType valueType, final Object value, final String format, final String targetId) {
        this.valueType = valueType;
        this.value = value;
        this.format = format;
        this.targetId = targetId;
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
}
