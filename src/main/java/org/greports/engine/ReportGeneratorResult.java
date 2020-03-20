package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.apache.log4j.Level;
import org.greports.exceptions.ReportEngineRuntimeException;
import org.greports.services.LoggerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReportGeneratorResult implements Serializable {
    private static final long serialVersionUID = 8220764494072805634L;

    private final boolean loggerEnabled;
    private transient LoggerService loggerService;
    private final List<ReportData> reportData = new ArrayList<>();
    private final transient ReportInjector reportInjector;
    private final List<String> deleteSheets = new ArrayList<>();

    public ReportGeneratorResult() {
        this(false, Level.ALL);
    }

    public ReportGeneratorResult(boolean loggerEnabled, Level level) {
        this.loggerEnabled = loggerEnabled;
        loggerService = new LoggerService(ReportGeneratorResult.class, loggerEnabled, level);
        reportInjector = new ReportInjector(reportData, deleteSheets, loggerEnabled);
    }

    protected void addData(ReportData data){
        reportData.add(data);
    }

    protected List<ReportData> getReportData() {
        return reportData;
    }

    private ReportData getReportDataBySheetName(final String sheetName) {
        return reportData.stream()
                .filter(rd -> rd.getSheetName().equals(sheetName))
                .findFirst()
                .orElse(null);
    }

    public ReportResultChanger getResultChanger(final String sheetName) {
        ReportData reportDataBySheetName = getReportDataBySheetName(sheetName);
        if(reportDataBySheetName == null){
            throw new ReportEngineRuntimeException(String.format("Sheet with name %s does not exist", sheetName), this.getClass());
        }
        return new ReportResultChanger(reportDataBySheetName, this);
    }

    public ReportGeneratorResult deleteSheet(final String sheetName) {
        if(sheetName == null){
            throw new ReportEngineRuntimeException("The parameter sheetName cannot be null", this.getClass());
        }
        deleteSheets.add(sheetName);
        return this;
    }

    /**
     * Deprecated. Use {@link ReportGeneratorResult#writeToPath(String)} instead.
     *
     * @param filePath File path
     * @throws IOException exception opening the stream to write to
     */
    @Deprecated
    public void writeToFile(String filePath) throws IOException {
        writeToPath(filePath);
    }

    /**
     * @param filePath File path
     * @throws IOException exception opening the stream to write to
     */
    public void writeToPath(String filePath) throws IOException {
        writeToFile(new File(filePath));
    }

    /**
     * @param file File to write to.
     * @throws IOException exception opening the stream to write to
     */
    public void writeToFile(File file) throws IOException {
        writeToFile(new FileOutputStream(file));
    }

    /**
     * Deprecated. Use {@link ReportGeneratorResult#writeToOutputStream(FileOutputStream)} instead.
     *
     * @param outputStream Output stream
     * @throws IOException exception opening the stream to write to
     */
    @Deprecated
    public void writeToFile(FileOutputStream outputStream) throws IOException {
        writeToOutputStream(outputStream);
    }

    /**
     * @param outputStream Output stream
     * @throws IOException exception opening the stream to write to
     */
    public void writeToOutputStream(FileOutputStream outputStream) throws IOException {
        Stopwatch injectStopwatch = Stopwatch.createStarted();
        loggerService.info("Data inject started...");
        reportInjector.inject();
        loggerService.info("Data inject successfully finished. Inject time: " + injectStopwatch.stop());

        loggerService.info("Write to file started...");
        final Stopwatch writeToStreamStopwatch = Stopwatch.createStarted();
        reportInjector.writeToFileOutputStream(outputStream);
        loggerService.info("Write to file successfully finished. Write time: " + writeToStreamStopwatch.stop());
    }
}
