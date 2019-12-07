package utils;

public class RectangleRange extends Tuple<VerticalRange, HorizontalRange> {

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