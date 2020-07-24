package org.greports.engine;

public enum ValueType {
    /**
     * Cell plain value.
     */
    PLAIN_VALUE,
    /**
     * Cell formula value.
     */
    FORMULA,
    /**
     * Cell formula value. Used to indicate to the engine that this type of cell
     * needs to be ignored during data inject, but this one needs to reindex
     * cell references used in the formula.
     */
    TEMPLATED_FORMULA,
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
