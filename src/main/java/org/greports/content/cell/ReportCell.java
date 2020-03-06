package org.greports.content.cell;

public interface ReportCell extends Cloneable {
    void setValue(final Object newValue);
    void setFormat(final String newFormat);
    Object clone() throws CloneNotSupportedException;
}
