import content.ReportData;

import java.io.*;

public class ReportGeneratorResult {

    private ReportData reportData;
    private ReportDataInjector dataInjector;

    ReportGeneratorResult(ReportData data){
        this.reportData = data;
        this.dataInjector = new ReportDataInjector(data);
    }

    public OutputStream writeToFileOutputStream(String path) throws IOException {
        dataInjector.inject();
        OutputStream outputStream = new FileOutputStream(new File(path));
        dataInjector.writeToFileOutputStream(outputStream);
        return outputStream;
    }
}
