package org.greports.content.cell;

import org.greports.engine.ValueType;

public class SpecialDataCell implements ReportCell, Cloneable {
    private final ValueType valueType;
    private Object value;
    private String format;
    private final String targetId;
    private final int columnWidth;
    private Object extraData;

    public SpecialDataCell(final ValueType valueType, final Object value, final String format, final String targetId) {
        this(valueType, value, format, targetId, 1);
    }

    public SpecialDataCell(final ValueType valueType, final Object value, final String format, final String targetId, final int columnWidth) {
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

    @Override
    public void setValue(final Object newValue) {
        this.value = newValue;
    }

    @Override
    public void setFormat(final String newFormat) {
        this.format = newFormat;
    }

    @Override
    public Object clone() {
        Object clone = this;
        try {
            clone = super.clone();
        } catch (CloneNotSupportedException ignored) {}
        return clone;
    }

    public Object getExtraData() {
        return extraData;
    }

    public SpecialDataCell setExtraData(final Object extraData) {
        this.extraData = extraData;
        return this;
    }
}
