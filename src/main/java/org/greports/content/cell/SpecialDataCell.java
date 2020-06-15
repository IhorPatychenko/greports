package org.greports.content.cell;

import org.greports.engine.ValueType;

public class SpecialDataCell extends AbstractReportCell implements ReportCell, Cloneable {
    private final ValueType valueType;
    private final String targetId;
    private final int columnWidth;
    private Object extraData;

    public SpecialDataCell(final ValueType valueType, final Object value, final String format, final String targetId) {
        this(valueType, value, format, targetId, 1);
    }

    public SpecialDataCell(final ValueType valueType, final Object value, final String format, final String targetId, final int columnWidth) {
        super(value, format);
        this.valueType = valueType;
        this.targetId = targetId;
        this.columnWidth = columnWidth;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public String getTargetId() {
        return targetId;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    public Object getExtraData() {
        return extraData;
    }

    public SpecialDataCell setExtraData(final Object extraData) {
        this.extraData = extraData;
        return this;
    }
}
