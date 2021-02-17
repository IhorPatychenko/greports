package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.apache.logging.log4j.Level;
import org.greports.exceptions.ReportEngineRuntimeException;
import org.greports.services.LoggerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportGeneratorResult implements Serializable {
    private static final long serialVersionUID = 8220764494072805634L;

    private static final Map<String, ReportResultChanger> _resultChangers = new HashMap<>();

    private final transient LoggerService loggerService;
    private final List<ReportData> reportData = new ArrayList<>();
    private final transient ReportInjector reportInjector;
    private final List<String> deleteSheets = new ArrayList<>();

    public ReportGeneratorResult(List<CustomFunction> functions, boolean evaluateFormulas, boolean loggerEnabled, Level level) {
        loggerService = new LoggerService(ReportGeneratorResult.class, loggerEnabled, level);
        reportInjector = new ReportInjector(reportData, deleteSheets, loggerEnabled, functions, evaluateFormulas, level);
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
        if(!_resultChangers.containsKey(sheetName)) {
            _resultChangers.put(sheetName, new ReportResultChanger(reportDataBySheetName, this));
        }
        return _resultChangers.get(sheetName);
    }

    public ReportGeneratorResult deleteSheet(final String sheetName) {
        if(sheetName == null){
            throw new ReportEngineRuntimeException("The parameter sheetName cannot be null", this.getClass());
        }
        deleteSheets.add(sheetName);
        return this;
    }

    public void setEvaluateFormulas(boolean evaluateFormulas) {
        this.reportInjector.setEvaluateFormulas(evaluateFormulas);
    }

    public void setForceFormulaRecalculation(boolean formulaRecalculation) {
        this.reportInjector.setForceFormulaRecalculation(formulaRecalculation);
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
        writeToOutputStream(new FileOutputStream(file));
    }

    /**
     * @param outputStream Output stream
     * @throws IOException exception opening the stream to write to
     */
    public void writeToOutputStream(FileOutputStream outputStream) throws IOException {
        reportInjector.inject();

        loggerService.info("Write to file started...");
        final Stopwatch writeToStreamStopwatch = Stopwatch.createStarted();
        reportInjector.writeToFileOutputStream(outputStream);
        loggerService.info("Write to file successfully finished. Write time: " + writeToStreamStopwatch.stop());
    }
}
