package org.greports.content.cell;

import org.greports.engine.ValueType;

public interface ReportCell extends Cloneable {
    ReportCell setValue(final Object newValue);
    ReportCell setFormat(final String newFormat);
    ReportCell setValueType(final ValueType valueType);
    Object clone() throws CloneNotSupportedException;
    Integer getColumnIndex();
}
