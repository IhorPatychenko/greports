package formula;

import java.util.StringJoiner;

public class FormulaBuilder {

    private Formula formula;
    private int cellsCount;
    public static final String FORMULA_TOKENIZER = "CELL";

    public FormulaBuilder(Formula formula, int cellsCount) {
        this.formula = formula;
        this.cellsCount = cellsCount;
    }

    public String build(){
        final String formulaJoiner = formula.isRanged() ? ":" : ",";
        final StringJoiner joiner = new StringJoiner(formulaJoiner);
        String[] formulaParts = formula.toString().split(FORMULA_TOKENIZER);
        for (int i = 0; i < cellsCount; i++) {
            joiner.add(FORMULA_TOKENIZER);
        }
        return formulaParts[0] + joiner.toString() + formulaParts[1];
    }
}
