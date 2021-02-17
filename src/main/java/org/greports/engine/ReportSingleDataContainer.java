package org.greports.engine;

public class ReportSingleDataContainer<T> extends ReportGenericDataContainer<T> {

    private T object;

    protected ReportSingleDataContainer(ReportData reportData, Class<T> clazz) {
        super(reportData, clazz);
    }

    public T getObject() {
        return object;
    }

    public ReportSingleDataContainer<T> setObject(T object) {
        this.object = object;
        return this;
    }
}
