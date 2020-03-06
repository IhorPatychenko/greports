package org.greports.positioning;

import java.io.Serializable;

public class HorizontalRange extends Range implements Serializable {

    private static final long serialVersionUID = -5397741564978460817L;

    public HorizontalRange(Integer start, Integer end) {
        super(start, end);
    }

    public Integer getStart(){
        return super.getStart();
    }

    public Integer getEnd(){
        return super.getEnd();
    }

    public void setStart(Integer start) {
        super.setStart(start);
    }

    public void setEnd(Integer end) {
        super.setEnd(end);
    }
}
