package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.apache.log4j.Level;
import org.greports.content.ReportData;
import org.greports.services.LoggerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportGeneratorResult {

    private final boolean loggerEnabled;
    private LoggerService loggerService;
    private ReportResultChanger resultChanger = new ReportResultChanger(this);

    public ReportGeneratorResult() {
        this(false, Level.ALL);
    }

    public ReportGeneratorResult(boolean loggerEnabled, Level level) {
        this.loggerEnabled = loggerEnabled;
        loggerService = new LoggerService(ReportGeneratorResult.class, loggerEnabled, level);
    }

    private final List<ReportData> reportData = new ArrayList<>();

    protected void addData(ReportData data){
        reportData.add(data);
    }

    protected List<ReportData> getReportData() {
        return reportData;
    }

    public ReportResultChanger getResultChanger() {
        return resultChanger;
    }

    public void writeToFile(String filePath) throws IOException {
        writeToFile(new File(filePath));
    }

    public void writeToFile(File file) throws IOException {
        writeToFile(new FileOutputStream(file));
    }

    public void writeToFile(FileOutputStream fileOutputStream) throws IOException {
        Stopwatch injectStopwatch = Stopwatch.createStarted();
        ReportInjector reportInjector = new ReportInjector(reportData, loggerEnabled);
        loggerService.info("Data inject started...");
        reportInjector.inject();
        loggerService.info("Data inject successfully finished. Inject time: " + injectStopwatch.stop());

        loggerService.info("Write to file started...");
        final Stopwatch writeToStreamStopwatch = Stopwatch.createStarted();
        reportInjector.writeToFileOutputStream(fileOutputStream);
        loggerService.info("Write to file successfully finished. Write time: " + writeToStreamStopwatch.stop());
    }
}
