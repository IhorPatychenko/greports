package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.apache.logging.log4j.Level;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greports.services.LoggerService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class Injector {

    private XSSFWorkbook currentWorkbook;
    private final List<Data> data;
    private final boolean loggerEnabled;
    protected LoggerService loggerService;
    private final List<String> deleteSheet;
    private boolean evaluateFormulas;
    private boolean forceFormulaRecalculation;

    public Injector(List<Data> data,
                    final List<String> deleteSheet,
                    boolean loggerEnabled,
                    boolean evaluateFormulas,
                    Level level) {
        this.data = data;
        this.loggerEnabled = loggerEnabled;
        this.deleteSheet = deleteSheet;
        this.evaluateFormulas = evaluateFormulas;
        this.loggerService = new LoggerService(Injector.class, this.loggerEnabled, level);
    }

    public void inject() {
        Stopwatch injectStopwatch = Stopwatch.createStarted();
        loggerService.info("Report(s) inject started...");
        for (Data entry : data) {
            if(currentWorkbook == null) {
                currentWorkbook = new XSSFWorkbook();
            }

            loggerService.info(String.format("Starting injecting data for report with name %s", entry.getReportName()));

            new DataInjector(currentWorkbook, entry, loggerEnabled, loggerService.getLevel()).inject();

            loggerService.info(String.format("Report data for report with name %s was successfully injected", entry.getReportName()));
        }

        for(final String sheetToDelete : deleteSheet) {
            currentWorkbook.removeSheetAt(currentWorkbook.getSheetIndex(sheetToDelete));
        }

        if(evaluateFormulas) {
            this.evaluateAllFormulas();
        }

        if(forceFormulaRecalculation) {
            currentWorkbook.setForceFormulaRecalculation(true);
        }

        loggerService.info("Report(s) inject successfully finished. Inject time: " + injectStopwatch.stop());
    }

    private void evaluateAllFormulas() {
        XSSFFormulaEvaluator.evaluateAllFormulaCells(currentWorkbook);
    }

    public void setEvaluateFormulas(boolean evaluateFormulas) {
        this.evaluateFormulas = evaluateFormulas;
    }

    public void setForceFormulaRecalculation(boolean formulaRecalculation) {
        this.forceFormulaRecalculation = formulaRecalculation;
    }

    void writeToFileOutputStream(OutputStream fileOutputStream) throws IOException {
        currentWorkbook.write(fileOutputStream);
        fileOutputStream.close();
        currentWorkbook.close();
    }
}
