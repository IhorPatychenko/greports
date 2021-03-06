package org.greports.interfaces.collectedvalues;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * An interfaces which purpose is to collect values from all object passed to the engine.
 * Those collected values can be manipulated
 * to be printed in a cell of a {@link org.greports.annotations.SpecialRow}.
 * This cell of a {@link org.greports.annotations.SpecialRow} need to have
 * an <code>COLLECTED_VALUE</code> as a {@link org.greports.engine.ValueType}.
 *
 * @param <I> input type
 * @param <O> output output
 */
public interface CollectedValues<I, O> {
    /**
     * The method returns a map which contains as a key a pair of
     * reportName and a targetId of columns whose values should be collected.
     * The {@link BooleanSupplier} indicates if this value should be collected.
     *
     * @return {@link Map}
     */
    Map<Pair<String, String>, BooleanSupplier> isCollectedValue();

    /**
     * The method returns a map which contains as a key a pair of
     * reportName and a targetId of columns whose values should be collected.
     * The I represents an input value to be obtained.
     *
     * @return {@link Map}
     */
    Map<Pair<String, String>, I> getCollectedValue();

    /**
     * The method returns a map which contains as a key a pair of
     * reportName and a targetId of columns whose values should be collected.
     * The O represents an output value to be obtained
     * from the list of input list.
     *
     * @return {@link Map}
     */
    Map<Pair<String, String>, O> getCollectedValuesResult(List<I> collectedValues);
}
