package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.apache.log4j.Level;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.udf.AggregatingUDFFinder;
import org.apache.poi.ss.formula.udf.DefaultUDFFinder;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greports.exceptions.ReportEngineRuntimeException;
import org.greports.services.LoggerService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ReportInjector {

    private XSSFWorkbook currentWorkbook;
    private final List<ReportData> reportData;
    private final boolean loggerEnabled;
    protected LoggerService loggerService;
    private final List<String> deleteSheet;
    private final List<CustomFunction> functions;
    private boolean evaluateFormulas;
    private boolean forceFormulaRecalculation;

    public ReportInjector(List<ReportData> reportData,
                          final List<String> deleteSheet,
                          boolean loggerEnabled,
                          List<CustomFunction> functions,
                          boolean evaluateFormulas,
                          Level level) {
        this.reportData = reportData;
        this.loggerEnabled = loggerEnabled;
        this.deleteSheet = deleteSheet;
        this.functions = functions;
        this.evaluateFormulas = evaluateFormulas;
        this.loggerService = new LoggerService(ReportInjector.class, this.loggerEnabled, level);
    }

    public void inject() {
        try {
            Stopwatch injectStopwatch = Stopwatch.createStarted();
            loggerService.info("Report(s) inject started...");
            for (ReportData data : reportData) {
                if(currentWorkbook == null) {
                    if(data.isReportWithTemplate()) {
                        currentWorkbook = (XSSFWorkbook) WorkbookFactory.create(data.getTemplateURL().openStream());
                    } else {
                        currentWorkbook = new XSSFWorkbook();
                    }
                    this.registerFunctions();
                }

                loggerService.info(String.format("Starting injecting data for report with name %s", data.getReportName()));

                if(data.isReportWithTemplate() || data.getConfiguration().isTemplatedInject()) {
                    new TemplateDataInjector(currentWorkbook, data, loggerEnabled, loggerService.getLevel()).inject();
                } else {
                    new RawDataInjector(currentWorkbook, data, loggerEnabled, loggerService.getLevel()).inject();
                }

                loggerService.info(String.format("Report data for report with name %s was successfully injected", data.getReportName()));
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

        } catch (InvalidFormatException e) {
            throw new ReportEngineRuntimeException("Error creating a workbook", e, this.getClass());
        } catch (IOException e) {
            throw new ReportEngineRuntimeException("Error opening a stream of template url", e, this.getClass());
        }
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

    private void registerFunctions() {
        final String[] functionNames = new String[functions.size()];
        final FreeRefFunction[] functionImpls = new FreeRefFunction[functions.size()];
        for(int i = 0; i < functions.size(); i++) {
            final CustomFunction customFunction = functions.get(i);
            functionNames[i] = customFunction.getFormulaName();
            functionImpls[i] = customFunction.getFreeRefFunction(currentWorkbook);
        }
        UDFFinder udfs = new DefaultUDFFinder(functionNames, functionImpls);
        UDFFinder udfToolpack = new AggregatingUDFFinder(udfs);

        currentWorkbook.addToolPack(udfToolpack);
    }

    void writeToFileOutputStream(OutputStream fileOutputStream) throws IOException {
        currentWorkbook.write(fileOutputStream);
        fileOutputStream.close();
        currentWorkbook.close();
    }
}
