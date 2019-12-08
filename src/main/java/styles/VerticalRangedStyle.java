package styles;

import utils.VerticalRange;

public class VerticalRangedStyle extends ReportStyle {

    private VerticalRange range;

    protected VerticalRangedStyle(ReportStyle rs) {
        super(rs);
    }

    public VerticalRangedStyle setRange(VerticalRange range) {
        this.range = range;
        return this;
    }

    public VerticalRange getRange() {
        return range;
    }
}
