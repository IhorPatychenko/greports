package org.greports.positioning;

import java.io.Serializable;

public class RectangleRange extends Tuple<VerticalRange, HorizontalRange> implements Serializable {

    private static final long serialVersionUID = 1361564754132331658L;

    public RectangleRange(VerticalRange verticalRange, HorizontalRange horizontalRange) {
        super(verticalRange, horizontalRange);
    }

    public VerticalRange getVerticalRange(){
        return super.getA();
    }

    public HorizontalRange getHorizontalRange(){
        return super.getB();
    }
}