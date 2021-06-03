package org.greports.positioning;

import java.io.Serializable;

public class RectangleRange extends Range<VerticalRange, HorizontalRange> implements Serializable {

    private static final long serialVersionUID = 1361564754132331658L;

    public RectangleRange(VerticalRange verticalRange, HorizontalRange horizontalRange) {
        super(verticalRange, horizontalRange);
    }

    @Override
    public RectangleRange toRectangleRange() {
        return this;
    }

    public VerticalRange getVerticalRange(){
        return super.getStart();
    }

    public HorizontalRange getHorizontalRange(){
        return super.getEnd();
    }
}