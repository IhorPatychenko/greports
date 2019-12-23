package formula;

public enum Formula {

    RANGED_SUM("SUM(CELL)", true),
    RANGED_MAX("MAX(CELL)", true),
    RANGER_MIN("MIN(CELL)", true),
    RANGER_AVERAGE("AVERAGE(CELL)", true),
    RANGER_COUNT("COUNT(CELL)", true),
    POSITIONED_SUM("SUM(CELL)", false),
    POSITIONED_MAX("MAX(CELL)", false),
    POSITIONED_MIN("MIN(CELL)", false),
    POSITIONED_AVERAGE("AVERAGE(CELL)", false),
    POSITIONED_COUNT("COUNT(CELL)", false);

    private String value;
    private boolean ranged;

    Formula(final String value, final boolean ranged){
        this.value = value;
        this.ranged = ranged;
    }

    public boolean isRanged() {
        return ranged;
    }

    @Override
    public String toString(){
        return this.value;
    }

}
