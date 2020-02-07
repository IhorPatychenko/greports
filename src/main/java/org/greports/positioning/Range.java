package org.greports.positioning;

public abstract class Range extends Tuple<Integer, Integer> {

    Range(Integer start, Integer end) {
        super(start, end);
    }

    protected Integer getStart(){
        return super.getA();
    }

    protected Integer getEnd(){
        return super.getB();
    }

    public void setStart(Integer start) {
        super.setA(start);
    }

    public void setEnd(Integer end) {
        super.setB(end);
    }
}
