package org.greports.engine;

import org.greports.content.ReportData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greports.exceptions.ReportEngineRuntimeException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

public class ReportInjector {

    private XSSFWorkbook currentWorkbook = new XSSFWorkbook();
    private final Collection<ReportData> reportData;
    private boolean loggerEnabled;

    public ReportInjector(Collection<ReportData> reportData, boolean loggerEnabled) {
        this.reportData = reportData;
        this.loggerEnabled = loggerEnabled;
    }

    public void inject() {
        try {
            for (ReportData data : reportData) {
                if (data.isReportWithTemplate()) {
                    currentWorkbook = (XSSFWorkbook) WorkbookFactory.create(data.getTemplateURL().openStream());
                    new ReportDataTemplateInjector(currentWorkbook, data, loggerEnabled).inject();
                } else {
                    new ReportDataRawInjector(currentWorkbook, data, loggerEnabled).inject();
                }
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
