package org.greports.content.cell;

/**
 * An abstract cell. Contains the common data of all cells in the report.
 */
public abstract class AbstractReportCell implements ReportCell, Cloneable {
    private Object value;
    private String format;
    private Integer columnIndex;

    public AbstractReportCell(Object value, String format) {
        this.value = value;
        this.format = format;
    }

    public Object getValue() {
        return value;
    }

    public String getFormat() {
        return format;
    }

    @Override
    public void setValue(Object newValue) {
        this.value = newValue;
    }

    @Override
    public void setFormat(final String newFormat) {
        this.format = newFormat;
    }

    public Integer getColumnIndex() {
        return columnIndex;
    }

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
