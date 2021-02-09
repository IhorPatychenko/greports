package org.greports.interfaces;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.function.BooleanSupplier;

public interface CollectedFormulaValues {
    Map<Pair<String, String>, BooleanSupplier> isCollectedFormulaValue();
}
