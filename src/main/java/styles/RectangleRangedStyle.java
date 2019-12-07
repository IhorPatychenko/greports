package styles;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import utils.RectangleRange;

public class RectangleRangedStyle extends ReportStyle {

    private RectangleRange range;

    RectangleRangedStyle(IndexedColors foregroundColor, IndexedColors fontColor, FillPatternType fillPattern, Boolean boldFont, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, BorderStyle borderTop, BorderStyle borderRight, BorderStyle borderBottom, BorderStyle borderLeft) {
        super(foregroundColor, fontColor, fillPattern, boldFont, horizontalAlignment, verticalAlignment, borderTop, borderRight, borderBottom, borderLeft);
    }

    public RectangleRange getRange() {
        return range;
    }

    public RectangleRangedStyle setRange(RectangleRange range) {
        this.range = range;
        return this;
    }
}
