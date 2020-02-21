package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.greports.content.ReportData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.greports.services.LoggerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ReportGeneratorResult {

    private final boolean loggerEnabled;
    private LoggerService loggerService;

    public ReportGeneratorResult() {
        this(false);
    }

    public ReportGeneratorResult(boolean loggerEnabled) {
        this.loggerEnabled = loggerEnabled;
        loggerService = new LoggerService(ReportGeneratorResult.class, loggerEnabled);
    }

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
        Stopwatch injectStopwatch = Stopwatch.createStarted();
        ReportInjector reportInjector = new ReportInjector(reportData, loggerEnabled);
        loggerService.info("Inject started...");
        reportInjector.inject();
        loggerService.info("Inject successfully finished. Inject time: " + injectStopwatch.stop());

        loggerService.info("Write to file started...");
        final Stopwatch writeToStreamStopwatch = Stopwatch.createStarted();
        reportInjector.writeToFileOutputStream(fileOutputStream);
        loggerService.info("Write to file successfully finished. Write time: " + writeToStreamStopwatch.stop());
    }
}
