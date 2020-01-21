package engine;

import content.ReportData;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReportGenerator {

    private ReportDataParser reportDataParser;
    private ReportGeneratorResult reportGeneratorResult = new ReportGeneratorResult();

    public ReportGenerator() {
        reportDataParser = new ReportDataParser();
    }

    public <T> ReportGenerator parse(Collection<T> collection, final String reportName, Class<T> clazz) throws IOException {
        return parse(collection, reportName, clazz, new HashMap<>());
    }

    public <T> ReportGenerator parse(Collection<T> collection, final String reportName, Class<T> clazz, Map<Integer, String> overriddenTitles) throws IOException {
        final ReportData data = reportDataParser.parse(collection, reportName, clazz, overriddenTitles).getData();
        reportGeneratorResult.addData(data);
        return this;
    }

    public ReportGeneratorResult getResult(){
        return this.reportGeneratorResult;
    }

}
