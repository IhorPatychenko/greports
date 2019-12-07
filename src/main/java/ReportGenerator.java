import java.util.Collection;
import java.util.Collections;

public class ReportGenerator {

    private ReportDataParser reportDataParser;

    public ReportGenerator(){
        this("en");
    }

    public ReportGenerator(String lang) {
        reportDataParser = new ReportDataParser(lang);
    }

    public <T> ReportGeneratorResult parse(T dto, final String reportName) throws Exception {
        return parse(Collections.singletonList(dto), reportName);
    }

    public <T> ReportGeneratorResult parse(Collection<T> collection, final String reportName) throws Exception {
        return new ReportGeneratorResult(reportDataParser.parse(collection, reportName).getData());
    }

}
