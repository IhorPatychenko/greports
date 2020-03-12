package org.greports.interfaces;

import org.greports.utils.Pair;

import java.util.Map;
import java.util.function.BooleanSupplier;

public interface CollectedFormulaValues {
    Map<Pair<String, String>, BooleanSupplier> isCollectedFormulaValue();
}
