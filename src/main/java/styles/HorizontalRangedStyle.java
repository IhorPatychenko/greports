package styles;

import utils.HorizontalRange;

public class HorizontalRangedStyle extends ReportStyle {

    private HorizontalRange range;

    protected HorizontalRangedStyle(ReportStyle rs) {
        super(rs);
    }

    public HorizontalRangedStyle setRange(HorizontalRange range) {
        this.range = range;
        return this;
    }

    public HorizontalRange getRange() {
        return range;
    }
}
