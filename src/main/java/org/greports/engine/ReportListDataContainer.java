package org.greports.engine;

import java.util.List;

public class ReportListDataContainer<T> extends ReportGenericDataContainer<T> {

    private List<T> data;

    public ReportListDataContainer(ReportData reportData, Class<T> clazz) {
        super(reportData, clazz);
    }

    public ReportGenericDataContainer<T> setData(List<T> data) {
        this.data = data;
        return this;
    }

    public List<T> getData() {
        return data;
    }
}
