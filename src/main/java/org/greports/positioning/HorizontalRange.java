package org.greports.positioning;

import java.io.Serializable;

public class HorizontalRange extends Tuple<Integer, Integer> implements Serializable {

    private static final long serialVersionUID = -5397741564978460817L;

    public HorizontalRange(Integer start, Integer end) {
        super(start, end);
    }

    @Override
    public RectangleRange toRectangeRange() {
        return new RectangleRange(new VerticalRange(0, null), this);
    }
}
