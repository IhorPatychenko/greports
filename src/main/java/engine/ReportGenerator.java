package engine;

import content.ReportData;

import java.util.Collection;

public class ReportGenerator {

    private ReportDataParser reportDataParser;
    private ReportGeneratorResult reportGeneratorResult = new ReportGeneratorResult();

    public ReportGenerator(){
        this("en");
    }

    public ReportGenerator(String lang) {
        reportDataParser = new ReportDataParser(lang);
    }

    public <T> ReportGenerator parse(Collection<T> collection, final String reportName) throws Exception {
        final ReportData data = reportDataParser.parse(collection, reportName).getData();
        reportGeneratorResult.addData(data);
        return this;
    }

    public ReportGeneratorResult getResult(){
        return this.reportGeneratorResult;
    }

}
