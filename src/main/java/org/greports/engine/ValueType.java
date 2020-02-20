package org.greports.engine;

public enum ValueType {
    /**
     * Plain value of cell.
     */
    PLAIN_VALUE,
    /**
     * Formula value of cell.
     */
    FORMULA,
    /**
     * Indicates that the reference to some method will be used to obtain the value.
     * When used an exact method name should be provided.
     * Examples: "getId", "getTotal", "toString"
     * Will be removed in version 2.0
     */
    @Deprecated
    METHOD,
    /**
     * Used in {@link org.greports.interfaces.CollectedValues} interface to collect all values from
     * all collection entries.
     */
    COLLECTED_VALUE
}
