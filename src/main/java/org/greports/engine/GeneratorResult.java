package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.apache.logging.log4j.Level;
import org.greports.exceptions.GreportsRuntimeException;
import org.greports.services.LoggerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneratorResult implements Serializable {
    private static final long serialVersionUID = 8220764494072805634L;

    private final Map<String, ResultChanger> _resultChangers = new HashMap<>();

    private final transient LoggerService loggerService;
    private final List<Data> data = new ArrayList<>();
    private final transient Injector injector;
    private final List<String> deleteSheets = new ArrayList<>();

    public GeneratorResult(boolean evaluateFormulas, boolean loggerEnabled, Level level) {
        loggerService = new LoggerService(GeneratorResult.class, loggerEnabled, level);
        injector = new Injector(data, deleteSheets, loggerEnabled, evaluateFormulas, level);
    }

    protected void addData(Data data){
        this.data.add(data);
    }

    protected List<Data> getReportData() {
        return data;
    }

    private Data getReportDataBySheetName(final String sheetName) {
        return data.stream()
                .filter(rd -> rd.getSheetName().equals(sheetName))
                .findFirst()
                .orElse(null);
    }

    public ResultChanger getResultChanger(final String sheetName) {
        Data dataBySheetName = getReportDataBySheetName(sheetName);
        if(dataBySheetName == null){
            throw new GreportsRuntimeException(String.format("Sheet with name %s does not exist", sheetName), this.getClass());
        }
        if(!_resultChangers.containsKey(sheetName)) {
            _resultChangers.put(sheetName, new ResultChanger(dataBySheetName, this));
        }
        return _resultChangers.get(sheetName);
    }


    void updateResultChangerSheetName(String oldSheetName, String newSheetName) {
        final ResultChanger resultChanger = _resultChangers.remove(oldSheetName);
        _resultChangers.put(newSheetName, resultChanger);
    }

    public GeneratorResult deleteSheet(final String sheetName) {
        if(sheetName == null){
            throw new GreportsRuntimeException("The parameter sheetName cannot be null", this.getClass());
        }
        deleteSheets.add(sheetName);
        return this;
    }

    public void setEvaluateFormulas(boolean evaluateFormulas) {
        this.injector.setEvaluateFormulas(evaluateFormulas);
    }

    public void setForceFormulaRecalculation(boolean formulaRecalculation) {
        this.injector.setForceFormulaRecalculation(formulaRecalculation);
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
        injector.inject();

        loggerService.info("Write to file started...");
        final Stopwatch writeToStreamStopwatch = Stopwatch.createStarted();
        injector.writeToFileOutputStream(outputStream);
        loggerService.info("Write to file successfully finished. Write time: " + writeToStreamStopwatch.stop());
    }
}
