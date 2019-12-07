package styles;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import utils.Position;

public class PositionedStyle extends ReportStyle {

    private Position position;

    public PositionedStyle(IndexedColors foregroundColor, IndexedColors fontColor, FillPatternType fillPattern, Boolean boldFont, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, BorderStyle borderTop, BorderStyle borderRight, BorderStyle borderBottom, BorderStyle borderLeft) {
        super(foregroundColor, fontColor, fillPattern, boldFont, horizontalAlignment, verticalAlignment, borderTop, borderRight, borderBottom, borderLeft);
    }

    public PositionedStyle setPosition(Position position) {
        this.position = position;
        return this;
    }

    public Position getPosition() {
        return position;
    }
}
