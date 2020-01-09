package engine;

import content.ReportData;

import java.io.IOException;
import java.util.Collection;

public class ReportGenerator {

    private ReportDataParser reportDataParser;
    private ReportGeneratorResult reportGeneratorResult = new ReportGeneratorResult();

    public ReportGenerator() {
        reportDataParser = new ReportDataParser();
    }

    public <T> ReportGenerator parse(Collection<T> collection, final String reportName, Class<T> clazz) throws IOException {
        final ReportData data = reportDataParser.parse(collection, reportName, clazz).getData();
        reportGeneratorResult.addData(data);
        return this;
    }

    public ReportGeneratorResult getResult(){
        return this.reportGeneratorResult;
    }

}
