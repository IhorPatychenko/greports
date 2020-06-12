package org.greports.content.cell;

/**
 * An abstract cell. Contains the common data of all cells in the report.
 */
public abstract class AbstractReportCell implements ReportCell, Cloneable {
    private final Float position;
    private Object value;
    private String format;

    public AbstractReportCell(Float position) {
        this.position = position;
    }

    public AbstractReportCell(Float position, Object value, String format) {
        this(position);
        this.value = value;
        this.format = format;
    }

    public Float getPosition() {
        return position;
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

    @Override
    public Object clone() {
        Object clone = this;
        try {
            clone = super.clone();
        } catch (CloneNotSupportedException ignored) {}
        return clone;
    }
}
