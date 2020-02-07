package org.greports.utils;

import java.util.Objects;

public class Pair<T, E> {
    private final T left;
    private final E right;

    private Pair(T left, E right) {
        this.left = left;
        this.right = right;
    }

    public static <T, E> Pair<T, E> of(T left, E right) {
        return new Pair<>(left, right);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<T, E> key = (Pair<T, E>) o;
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