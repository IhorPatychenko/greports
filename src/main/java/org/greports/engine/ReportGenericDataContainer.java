package org.greports.engine;

import org.greports.utils.Translator;

import java.util.ArrayList;
import java.util.List;

public abstract class ReportGenericDataContainer<T> {

    private final ReportData reportData;
    private Translator translator;
    private final List<ReportData> subreportsData = new ArrayList<>();
    private ReportConfigurator configurator;
    private final Class<T> clazz;

    protected ReportGenericDataContainer(ReportData reportData, Class<T> clazz) {
        this.reportData = reportData;
        this.clazz = clazz;
    }

    public ReportData getReportData() {
        return reportData;
    }

    public ReportGenericDataContainer<T> setTranslator(Translator translator) {
        this.translator = translator;
        return this;
    }

    public Translator getTranslator() {
        return translator;
    }

    public List<ReportData> getSubreportsData() {
        return subreportsData;
    }

    public ReportGenericDataContainer<T> setConfigurator(ReportConfigurator configurator) {
        this.configurator = configurator;
        return this;
    }

    public ReportConfigurator getConfigurator() {
        return configurator;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
