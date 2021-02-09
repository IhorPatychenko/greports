package org.greports.interfaces;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public interface CollectedValues<I, O> {
    Map<Pair<String, String>, BooleanSupplier> isCollectedValue();
    Map<Pair<String, String>, I> getCollectedValue();
    Map<Pair<String, String>, O> getCollectedValuesResult(List<I> collectedValues);
}
