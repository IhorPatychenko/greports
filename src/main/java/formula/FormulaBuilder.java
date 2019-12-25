package formula;

import java.util.StringJoiner;

public class FormulaBuilder {

    private String formula;
    private boolean isRangedFormula;
    private int cellsCount;
    public static final String FORMULA_TOKENIZER = "CELL";

    public FormulaBuilder(String formula, boolean isRangedFormula, int cellsCount) {
        this.formula = formula;
        this.isRangedFormula = isRangedFormula;
        this.cellsCount = cellsCount;
    }

    public String build(){
        final String formulaJoiner = isRangedFormula ? ":" : ",";
        final StringJoiner joiner = new StringJoiner(formulaJoiner);
        for (int i = 0; i < cellsCount; i++) {
            joiner.add(FORMULA_TOKENIZER);
        }
        return formula + "(" + joiner.toString() + ")";
    }
}
