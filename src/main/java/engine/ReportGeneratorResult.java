package engine;

import content.ReportData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ReportGeneratorResult {

    private final Collection<ReportData> reportData = new ArrayList<>();

    void addData(ReportData data){
        reportData.add(data);
    }

    public void writeToFile(String filePath) throws IOException, InvalidFormatException {
        writeToFile(new File(filePath));
    }

    public void writeToFile(File file) throws IOException, InvalidFormatException {
        writeToFile(new FileOutputStream(file));
    }

    public void writeToFile(FileOutputStream fileOutputStream) throws IOException, InvalidFormatException {
        ReportInjector reportInjector = new ReportInjector(reportData);
        reportInjector.inject();
        reportInjector.writeToFileOutputStream(fileOutputStream);
    }
}
