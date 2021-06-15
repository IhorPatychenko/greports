package org.greports.engine;

public class SingleDataContainer<T> extends DataContainer<T> {

    private T object;

    protected SingleDataContainer(Data data, Class<T> clazz) {
        super(data, clazz);
    }

    public T getObject() {
        return object;
    }

    public SingleDataContainer<T> setObject(T object) {
        this.object = object;
        return this;
    }
}
