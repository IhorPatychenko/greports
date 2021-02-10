package org.greports.content.cell;

import org.greports.engine.ValueType;

public class SpecialDataCell extends AbstractReportCell implements ReportCell {
    private final ValueType valueType;
    private final String targetId;
    private final int columnWidth;
    private Object valuesById;

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

    public Object getValuesById() {
        return valuesById;
    }

    public SpecialDataCell setValuesById(final Object valuesById) {
        this.valuesById = valuesById;
        return this;
    }
}
