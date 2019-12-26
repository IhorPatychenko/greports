package utils;

import java.util.Objects;

public class Pair<T, E> {
    private final T left;
    private final E right;

    public Pair(T left, E right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair key = (Pair) o;
        return left.equals(key.left) && right.equals(key.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    public T getLeft() {
        return left;
    }

    public E getRight() {
        return right;
    }
}