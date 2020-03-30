package org.greports.engine;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greports.exceptions.ReportEngineRuntimeException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ReportInjector {

    private XSSFWorkbook currentWorkbook = new XSSFWorkbook();
    private final List<ReportData> reportData;
    private boolean loggerEnabled;
    private final List<String> deleteSheet;

    public ReportInjector(List<ReportData> reportData, final List<String> deleteSheet, boolean loggerEnabled) {
        this.reportData = reportData;
        this.loggerEnabled = loggerEnabled;
        this.deleteSheet = deleteSheet;
    }

    public void inject() {
        try {
            for (ReportData data : reportData) {
                if(!data.getConfiguration().isForceRawInject()) {
                    if(data.isReportWithTemplate() || data.getConfiguration().isUseExistingSheet()) {
                        currentWorkbook = (XSSFWorkbook) WorkbookFactory.create(data.getTemplateURL().openStream());
                    }
                    new TemplateDataInjector(currentWorkbook, data, loggerEnabled).inject();
                } else {
                    if(data.isReportWithTemplate()) {
                        currentWorkbook = (XSSFWorkbook) WorkbookFactory.create(data.getTemplateURL().openStream());
                    }
                    new RawDataInjector(currentWorkbook, data, loggerEnabled).inject();
                }
            }
            for(final String deleteSheet : deleteSheet) {
                currentWorkbook.removeSheetAt(currentWorkbook.getSheetIndex(deleteSheet));
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
