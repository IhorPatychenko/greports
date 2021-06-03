package org.greports.content.cell;

import lombok.Getter;
import org.greports.engine.ValueType;

/**
 * An abstract cell. Contains the common data of all cells in the report.
 */
@Getter
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
