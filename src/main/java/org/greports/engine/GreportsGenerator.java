package org.greports.engine;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.greports.exceptions.GreportsReflectionException;
import org.greports.exceptions.GreportsRuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GreportsGenerator {

    private final Map<Pair<Class<?>, String>, Configurator> _configurators = new HashMap<>();

    private final boolean loggerEnabled;
    private final Level level;
    private final GeneratorResult generatorResult;

    public GreportsGenerator() {
        this(false, Level.ALL);
    }

    public GreportsGenerator(boolean loggerEnabled, Level level) {
        this.loggerEnabled = loggerEnabled;
        this.level = level;
        this.generatorResult = new GeneratorResult(false, loggerEnabled, level);
    }

    public <T> GreportsGenerator parse(final List<T> list, final String reportName, Class<T> clazz) throws GreportsReflectionException {
        DataParser<T> dataParser = new DataParser<>(this.loggerEnabled, this.level);
        final Data data = dataParser.parse(list, reportName, clazz, getConfigurator(clazz, reportName)).getContainer().getReportData();
        generatorResult.addData(data);
        return this;
    }

    public <T> GreportsGenerator parseSingleObject(final T object, final String reportName, Class<T> clazz) throws GreportsReflectionException {
        SingleDataParser<T> singleDataParser = new SingleDataParser<>(this.loggerEnabled, this.level);
        final Data data = singleDataParser.parse(object, reportName, clazz, getConfigurator(clazz, reportName)).getContainer().getReportData();
        generatorResult.addData(data);
        return this;
    }

    public GreportsGenerator parseReport(final Data data) {
        if(data == null) {
            throw new GreportsRuntimeException("reportData cannot be null", this.getClass());
        }
        generatorResult.addData(data);
        return this;
    }

    public GreportsGenerator setEvaluateFormulas(boolean evaluateFormulas) {
        this.generatorResult.setEvaluateFormulas(evaluateFormulas);
        return this;
    }

    public GreportsGenerator setForceFormulaRecalculation(boolean formulaRecalculation) {
        this.generatorResult.setForceFormulaRecalculation(formulaRecalculation);
        return this;
    }

    public Configurator getConfigurator(final Class<?> clazz, final String reportName){
        final Pair<Class<?>, String> key = Pair.of(clazz, reportName);
        if(!_configurators.containsKey(key)){
            _configurators.put(key, new Configurator(this));
        }
        return _configurators.get(key);
    }

    public GeneratorResult getResult(){
        return this.generatorResult;
    }

}
