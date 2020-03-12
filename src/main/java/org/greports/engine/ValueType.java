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
     */
    METHOD,
    /**
     * Used in {@link org.greports.interfaces.CollectedValues} interface to collect all values from
     * all collection entries.
     */
    COLLECTED_VALUE,
    /**
     * Used in {@link org.greports.interfaces.CollectedFormulaValues} interface to collect all values from
     * all entries and put them as entries of the formula provided.
     */
    COLLECTED_FORMULA_VALUE
}
