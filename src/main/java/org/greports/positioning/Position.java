package org.greports.positioning;

import java.io.Serializable;

public class Position extends Tuple<Integer, Integer> implements Serializable {
    private static final long serialVersionUID = 1633158484373979912L;

    public Position(Integer row, Integer column) {
        super(row, column);
    }

    public Integer getRow(){
        return super.getStart();
    }

    public Integer getColumn(){
        return super.getEnd();
    }

    public Position setRow(Integer row) {
        super.setStart(row);
        return this;
    }

    public Position setColumn(Integer column) {
        super.setEnd(column);
        return this;
    }

    @Override
    public RectangleRange toRectangeRange() {
        return new RectangleRange(new VerticalRange(getRow(), getRow()), new HorizontalRange(getColumn(), getColumn()));
    }
}
