package org.greports.positioning;

import java.io.Serializable;

public abstract class Range<T extends Serializable, E extends Serializable> implements Serializable {

    private static final long serialVersionUID = 8980520714685635759L;
    private T start;
    private E end;

    protected Range(T start, E end) {
        this.start = start;
        this.end = end;
    }

    public T getStart(){
        return start;
    }

    public E getEnd(){
        return end;
    }

    public void setStart(T start) {
        this.start = start;
    }

    public void setEnd(E end) {
        this.end = end;
    }

    public abstract RectangleRange toRectangeRange();
}
