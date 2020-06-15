package org.greports.engine;

import org.apache.log4j.Level;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.exceptions.ReportEngineRuntimeException;
import org.greports.utils.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportGenerator {

    private final static Map<Pair<Class<?>, String>, ReportConfigurator> _configurators = new HashMap<>();

    private final ReportDataParser reportDataParser;
    private final ReportSingleDataParser reportSingleDataParser;
    private final ReportGeneratorResult reportGeneratorResult;

    public ReportGenerator() {
        this(false, Level.ALL);
    }

    public ReportGenerator(boolean loggerEnabled, Level level) {
        reportDataParser = new ReportDataParser(loggerEnabled, level);
        reportSingleDataParser = new ReportSingleDataParser(loggerEnabled, level);
        reportGeneratorResult = new ReportGeneratorResult(loggerEnabled, level);
    }

    public <T> ReportGenerator parse(final List<T> collection, final String reportName, Class<T> clazz) throws ReportEngineReflectionException {
        final ReportData data = reportDataParser.parse(clazz, reportName, collection, getConfigurator(clazz, reportName)).getData();
        reportGeneratorResult.addData(data);
        return this;
    }

    public <T> ReportGenerator parseSingleObject(final T object, final String reportName, Class<T> clazz) throws ReportEngineReflectionException {
        final ReportData data = reportSingleDataParser.parse(object, reportName, clazz).getData();
        reportGeneratorResult.addData(data);
        return this;
    }

    public ReportGenerator parseReport(final ReportData reportData) {
        if(reportData == null) {
            throw new ReportEngineRuntimeException("reportData cannot be null", this.getClass());
        }
        reportGeneratorResult.addData(reportData);
        return this;
    }

    public ReportConfigurator getConfigurator(final Class<?> clazz, final String reportName){
        final Pair<Class<?>, String> key = Pair.of(clazz, reportName);
        if(!_configurators.containsKey(key)){
            _configurators.put(key, new ReportConfigurator(this));
        }
        return _configurators.get(key);
    }

    public ReportGeneratorResult getResult(){
        return this.reportGeneratorResult;
    }

}
