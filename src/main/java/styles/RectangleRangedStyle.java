package styles;

import positioning.RectangleRange;

public class RectangleRangedStyle extends ReportStyle {

    private RectangleRange range;

    protected RectangleRangedStyle(ReportStyle rs) {
        super(rs);
    }

    public RectangleRange getRange() {
        return range;
    }

    public RectangleRangedStyle setRange(RectangleRange range) {
        this.range = range;
        return this;
    }
}
