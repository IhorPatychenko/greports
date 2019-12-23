package content.cell;

import engine.ValueType;

public class ReportDataSpecialRowCell {
    private ValueType valueType;
    private Object value;
    private String format;
    private String targetId;

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
