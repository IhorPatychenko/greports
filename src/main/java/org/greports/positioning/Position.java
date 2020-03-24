package org.greports.positioning;

import java.io.Serializable;

public class Position extends Tuple<Integer, Integer> implements Serializable {
    private static final long serialVersionUID = 1633158484373979912L;

    public Position(Integer row, Integer column) {
        super(row, column);
    }

    public Integer getRow(){
        return super.getA();
    }

    public Integer getColumn(){
        return super.getB();
    }

    public Position setRow(Integer row) {
        super.setA(row);
        return this;
    }

    public Position setColumn(Integer column) {
        super.setB(column);
        return this;
    }
}
