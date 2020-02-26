package org.greports.engine;

import org.apache.log4j.Level;
import org.greports.content.ReportData;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.utils.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReportGenerator {

    private final Map<Pair<Class<?>, String>, ReportConfigurator> _configurators = new HashMap<>();

    private final ReportDataParser reportDataParser;
    private final ReportGeneratorResult reportGeneratorResult;

    public ReportGenerator() {
        this(false, Level.ALL);
    }

    public ReportGenerator(boolean loggerEnabled, Level level) {
        reportDataParser = new ReportDataParser(loggerEnabled, level);
        reportGeneratorResult = new ReportGeneratorResult(loggerEnabled);
    }

    public <T> ReportGenerator parse(Collection<T> collection, final String reportName, Class<T> clazz) throws ReportEngineReflectionException {
        final ReportData data = reportDataParser.parse(collection, reportName, clazz, getConfigurator(clazz, reportName)).getData();
        reportGeneratorResult.addData(data);
        return this;
    }

    public ReportConfigurator getConfigurator(Class<?> clazz, String reportName){
        final Pair<Class<?>, String> key = Pair.of(clazz, reportName);
        if(!_configurators.containsKey(key)){
            _configurators.put(key, new ReportConfigurator());
        }
        return _configurators.get(key);
    }

    public ReportGeneratorResult getResult(){
        return this.reportGeneratorResult;
    }

}
