package org.greports.positioning;

import java.io.Serializable;

public class HorizontalRange extends Range implements Serializable {

    private static final long serialVersionUID = -5397741564978460817L;

    public HorizontalRange(Integer start, Integer end) {
        super(start, end);
    }

    @Override
    public Integer getStart(){
        return super.getStart();
    }

    @Override
    public Integer getEnd(){
        return super.getEnd();
    }
}
