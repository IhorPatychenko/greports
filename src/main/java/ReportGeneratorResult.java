import content.ReportData;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

public class ReportGeneratorResult {

    private Collection<ReportData> reportData = new ArrayList<>();
    private ReportDataInjector dataInjector;

    ReportGeneratorResult(){
        //this.reportData = data;
        //this.dataInjector = new ReportDataInjector(data);
    }

    public void addData(ReportData data){
        reportData.add(data);
    }

    public OutputStream writeToFileOutputStream(String path) throws IOException {
        dataInjector = new ReportDataInjector(reportData);
        dataInjector.inject();
        OutputStream outputStream = new FileOutputStream(new File(path));
        dataInjector.writeToFileOutputStream(outputStream);
        return outputStream;
    }
}
