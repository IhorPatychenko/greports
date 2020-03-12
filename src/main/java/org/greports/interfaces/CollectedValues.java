package org.greports.interfaces;

import org.greports.utils.Pair;

import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public interface CollectedValues<I, O> {
    Map<Pair<String, String>, BooleanSupplier> isCollectedValue();
    Map<Pair<String, String>, I> getCollectedValue();
    O getCollectedValuesResult(List<I> collectedValues);
}
