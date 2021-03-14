package org.greports.content.cell;

import org.greports.engine.ValueType;

public interface ReportCell extends Cloneable {
    /**
     * Sets a new value of the cell.
     *
     * @param newValue new cell value
     * @return {@link ReportCell}
     */
    ReportCell setValue(final Object newValue);

    /**
     * Sets a new format of the cell.
     *
     * @param newFormat new cell value
     * @return {@link ReportCell}
     */
    ReportCell setFormat(final String newFormat);

    /**
     * Sets a new value type of the cell.
     *
     * @param valueType new cell value
     * @return {@link ReportCell}
     */
    ReportCell setValueType(final ValueType valueType);

    /**
     * Clones the cell.
     *
     * @return {@link Object}
     * @throws CloneNotSupportedException if clone is not supported
     */
    Object clone() throws CloneNotSupportedException;

    /**
     * Returns a column index of the cell.
     *
     * @return {@link Integer}
     */
    Integer getColumnIndex();
}
