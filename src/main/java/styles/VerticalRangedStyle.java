package styles;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import utils.VerticalRange;

public class VerticalRangedStyle extends ReportStyle {

    private VerticalRange range;

    public VerticalRangedStyle(IndexedColors foregroundColor, IndexedColors fontColor, FillPatternType fillPattern, Boolean boldFont, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, BorderStyle borderTop, BorderStyle borderRight, BorderStyle borderBottom, BorderStyle borderLeft) {
        super(foregroundColor, fontColor, fillPattern, boldFont, horizontalAlignment, verticalAlignment, borderTop, borderRight, borderBottom, borderLeft);
    }

    public VerticalRangedStyle setRange(VerticalRange range) {
        this.range = range;
        return this;
    }

    public VerticalRange getRange() {
        return range;
    }
}
