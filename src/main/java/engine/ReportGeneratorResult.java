package engine;

import content.ReportData;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

public class ReportGeneratorResult {

    private Collection<ReportData> reportData = new ArrayList<>();

    void addData(ReportData data){
        reportData.add(data);
    }

    public void writeToFile(String filePath) throws IOException {
        writeToFile(new File(filePath));
    }

    public void writeToFile(File file) throws IOException {
        writeToFile(new FileOutputStream(file));
    }

    public void writeToFile(FileOutputStream fileOutputStream) throws IOException {
        ReportDataInjector dataInjector = new ReportDataInjector(reportData);
        dataInjector.inject();
        dataInjector.writeToFileOutputStream(fileOutputStream);
    }
}
