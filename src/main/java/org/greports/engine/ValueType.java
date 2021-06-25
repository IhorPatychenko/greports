package org.greports.engine;

import org.greports.interfaces.collectedvalues.CollectedFormulaValues;
import org.greports.interfaces.collectedvalues.CollectedValues;

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
     * Indicates that the reference to some method will be used to obtain the value.
     * When used an exact method name should be provided.
     * Examples: "getId", "getTotal", "toString"
     */
    METHOD,
    /**
     * Used in {@link CollectedValues} interface to collect all values from
     * all collection entries.
     */
    COLLECTED_VALUE,
    /**
     * Used in {@link CollectedFormulaValues} interface to collect all values from
     * all entries and put them as entries of the formula provided.
     */
    COLLECTED_FORMULA_VALUE,
    /**
     * Completely ignores the column content and it's title if set,
     * but conserves it's physical position in excel file.
     */
    IGNORED_VALUE
}
