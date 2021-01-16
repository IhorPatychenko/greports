package org.greports.positioning;

import java.io.Serializable;

public class VerticalRange extends Tuple<Integer, Integer> implements Serializable {
    private static final long serialVersionUID = 1043724725272422106L;

    public VerticalRange(Integer start, Integer end) {
        super(start, end);
    }

    @Override
    public RectangleRange toRectangeRange() {
        return new RectangleRange(this, new HorizontalRange(0, null));
    }
}
