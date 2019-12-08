package utils;

public abstract class Tuple<T, E> {

    private T a;
    private E b;

    public Tuple(T a, E b) {
        this.a = a;
        this.b = b;
    }

    protected T getA() {
        return a;
    }

    protected E getB() {
        return b;
    }

    protected void setA(T a){
        this.a = a;
    }

    protected void setB(E b) {
        this.b = b;
    }
}
