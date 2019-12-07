package utils;

public abstract class Tuple<T, E> {

    private final T a;
    private final E b;

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
}
