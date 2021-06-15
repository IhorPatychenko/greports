package org.greports.engine;

import java.util.List;

public class ListDataContainer<T> extends DataContainer<T> {

    private List<T> data;

    public ListDataContainer(Data data, Class<T> clazz) {
        super(data, clazz);
    }

    public DataContainer<T> setData(List<T> data) {
        this.data = data;
        return this;
    }

    public List<T> getData() {
        return data;
    }
}
