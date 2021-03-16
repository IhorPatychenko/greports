package org.greports.content.cell;

import org.greports.engine.ValueType;

/**
 * An abstract cell. Contains the common data of all cells in the report.
 */
public abstract class AbstractReportCell implements ReportCell, Cloneable {
    private Object value;
    private String format;
    private Integer columnIndex;
    private ValueType valueType;

    /**
     * @param value cell value
     * @param format cell format
     * @param valueType cell value type
     *
     * @see ValueType
     */
    protected AbstractReportCell(Object value, String format, ValueType valueType) {
        this.value = value;
        this.format = format;
        this.valueType = valueType;
    }

    /**
     * @return {@link Object} cell value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @return {@link String} cell format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @return {@link ValueType} cell value type
     */
    public ValueType getValueType() {
        return valueType;
    }

    @Override
    public ReportCell setValue(Object newValue) {
        this.value = newValue;
        return this;
    }

    @Override
    public ReportCell setFormat(final String newFormat) {
        this.format = newFormat;
        return this;
    }

    /**
     * @param valueType new cell value
     * @return {@link ReportCell}
     */
    @Override
    public ReportCell setValueType(ValueType valueType) {
        this.valueType = valueType;
        return this;
    }

    /**
     * @return {@link Integer} column index
     */
    public Integer getColumnIndex() {
        return columnIndex;
    }

    /**
     * @param columnIndex column index
     */
    public void setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public Object clone() {
        Object clone = this;
        try {
            clone = super.clone();
        } catch (CloneNotSupportedException ignored) {}
        return clone;
    }
}
