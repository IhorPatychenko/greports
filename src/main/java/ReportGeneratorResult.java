import content.ReportData;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

public class ReportGeneratorResult {

    private Collection<ReportData> reportData = new ArrayList<>();

    void addData(ReportData data){
        reportData.add(data);
    }

    public OutputStream writeToFile(String path) throws IOException {
        ReportDataInjector dataInjector = new ReportDataInjector(reportData);
        dataInjector.inject();
        OutputStream outputStream = new FileOutputStream(new File(path));
        dataInjector.writeToFileOutputStream(outputStream);
        return outputStream;
    }
}
