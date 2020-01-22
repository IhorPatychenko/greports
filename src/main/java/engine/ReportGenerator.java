package engine;

import content.ReportData;
import utils.Pair;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReportGenerator {

    private Map<Pair<Class<?>, String>, ReportConfigurator> _configurators = new HashMap<>();

    private ReportDataParser reportDataParser;
    private ReportGeneratorResult reportGeneratorResult = new ReportGeneratorResult();

    public ReportGenerator() {
        reportDataParser = new ReportDataParser();
    }

    public <T> ReportGenerator parse(Collection<T> collection, final String reportName, Class<T> clazz) throws IOException {
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
