package styles;

import utils.Position;

public class PositionedStyle extends ReportStyle {

    private Position position;

    protected PositionedStyle(ReportStyle rs) {
        super(rs);
    }

    public PositionedStyle setPosition(Position position) {
        this.position = position;
        return this;
    }

    public Position getPosition() {
        return position;
    }
}
