package org.greports.engine;

import org.greports.utils.Translator;

import java.util.ArrayList;
import java.util.List;

public abstract class DataContainer<T> {

    private final Data data;
    private Translator translator;
    private final List<Data> subreportsData = new ArrayList<>();
    private Configurator configurator;
    private final Class<T> clazz;

    protected DataContainer(Data data, Class<T> clazz) {
        this.data = data;
        this.clazz = clazz;
    }

    public Data getReportData() {
        return data;
    }

    public DataContainer<T> setTranslator(Translator translator) {
        this.translator = translator;
        return this;
    }

    public Translator getTranslator() {
        return translator;
    }

    public List<Data> getSubreportsData() {
        return subreportsData;
    }

    public DataContainer<T> setConfigurator(Configurator configurator) {
        this.configurator = configurator;
        return this;
    }

    public Configurator getConfigurator() {
        return configurator;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
