package org.greports.engine;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.exceptions.ReportEngineRuntimeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportGenerator {

    private final Map<Pair<Class<?>, String>, ReportConfigurator> _configurators = new HashMap<>();

    private final boolean loggerEnabled;
    private final Level level;
    private final ReportGeneratorResult reportGeneratorResult;
    private final List<CustomFunction> functions = new ArrayList<>();

    public ReportGenerator() {
        this(false, Level.ALL);
    }

    public ReportGenerator(boolean loggerEnabled, Level level) {
        this.loggerEnabled = loggerEnabled;
        this.level = level;
        this.reportGeneratorResult = new ReportGeneratorResult(this.functions, false, loggerEnabled, level);
    }

    public <T> ReportGenerator parse(final List<T> list, final String reportName, Class<T> clazz) throws ReportEngineReflectionException {
        ReportDataParser<T> reportDataParser = new ReportDataParser<>(this.loggerEnabled, this.level);
        final ReportData reportData = reportDataParser.parse(list, reportName, clazz, getConfigurator(clazz, reportName)).getContainer().getReportData();
        reportGeneratorResult.addData(reportData);
        return this;
    }

    public <T> ReportGenerator parseSingleObject(final T object, final String reportName, Class<T> clazz) throws ReportEngineReflectionException {
        ReportSingleDataParser<T> reportSingleDataParser = new ReportSingleDataParser<>(this.loggerEnabled, this.level);
        final ReportData data = reportSingleDataParser.parse(object, reportName, clazz, getConfigurator(clazz, reportName)).getContainer().getReportData();
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

    public ReportGenerator registerFunction(CustomFunction function) {
        functions.add(function);
        return this;
    }

    public ReportGenerator setEvaluateFormulas(boolean evaluateFormulas) {
        this.reportGeneratorResult.setEvaluateFormulas(evaluateFormulas);
        return this;
    }

    public ReportGenerator setForceFormulaRecalculation(boolean formulaRecalculation) {
        this.reportGeneratorResult.setForceFormulaRecalculation(formulaRecalculation);
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
