package org.greports.positioning;

public class Position extends Tuple<Integer, Integer> {

    public Position(Integer row, Integer column) {
        super(row, column);
    }

    public Integer getRow(){
        return super.getA();
    }

    public Integer getColumn(){
        return super.getB();
    }

    public void setRow(Integer row) {
        super.setA(row);
    }

    public void setColumn(Integer column) {
        super.setB(column);
    }
}
