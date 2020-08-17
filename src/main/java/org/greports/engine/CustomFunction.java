package org.greports.engine;

import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.usermodel.Workbook;

public interface CustomFunction {
    String getFormulaName();
    FreeRefFunction getFreeRefFunction(Workbook currentWorkbook);
}
