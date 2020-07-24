package org.greports.engine;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greports.exceptions.ReportEngineRuntimeException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ReportInjector {

    private XSSFWorkbook currentWorkbook;
    private final List<ReportData> reportData;
    private final boolean loggerEnabled;
    private final List<String> deleteSheet;

    public ReportInjector(List<ReportData> reportData, final List<String> deleteSheet, boolean loggerEnabled) {
        this.reportData = reportData;
        this.loggerEnabled = loggerEnabled;
        this.deleteSheet = deleteSheet;
    }

    public void inject() {
        try {
            for (ReportData data : reportData) {
                if(currentWorkbook == null && data.isReportWithTemplate()) {
                    currentWorkbook = (XSSFWorkbook) WorkbookFactory.create(data.getTemplateURL().openStream());
                } else if(currentWorkbook == null) {
                    currentWorkbook = new XSSFWorkbook();
                }

                if(data.isReportWithTemplate() && data.getConfiguration().isTemplatedInject()) {
                    new TemplateDataInjector(currentWorkbook, data, loggerEnabled).inject();
                } else {
                    new RawDataInjector(currentWorkbook, data, loggerEnabled).inject();
                }
            }
            for(final String sheetToDelete : deleteSheet) {
                currentWorkbook.removeSheetAt(currentWorkbook.getSheetIndex(sheetToDelete));
            }
        } catch (InvalidFormatException e) {
            throw new ReportEngineRuntimeException("Error creating a workbook", e, ReportInjector.class);
        } catch (IOException e) {
            throw new ReportEngineRuntimeException("Error opening a stream of template url", e, ReportInjector.class);
        }
    }

    void writeToFileOutputStream(OutputStream fileOutputStream) throws IOException {
        currentWorkbook.write(fileOutputStream);
        fileOutputStream.close();
        currentWorkbook.close();
    }
}
