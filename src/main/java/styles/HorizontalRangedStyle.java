package styles;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import utils.HorizontalRange;

public class HorizontalRangedStyle extends ReportStyle {

    private HorizontalRange range;

    public HorizontalRangedStyle(IndexedColors foregroundColor, IndexedColors fontColor, FillPatternType fillPattern, Boolean boldFont, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, BorderStyle borderTop, BorderStyle borderRight, BorderStyle borderBottom, BorderStyle borderLeft) {
        super(foregroundColor, fontColor, fillPattern, boldFont, horizontalAlignment, verticalAlignment, borderTop, borderRight, borderBottom, borderLeft);
    }

    public HorizontalRangedStyle setRange(HorizontalRange range) {
        this.range = range;
        return this;
    }

    public HorizontalRange getRange() {
        return range;
    }
}
