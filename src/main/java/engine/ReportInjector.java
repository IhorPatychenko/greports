package engine;

import content.ReportData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

public class ReportInjector {

    private XSSFWorkbook currentWorkbook = new XSSFWorkbook();
    private final Collection<ReportData> reportData;

    public ReportInjector(Collection<ReportData> reportData) {
        this.reportData = reportData;
    }

    public void inject() throws IOException, InvalidFormatException {
        for (ReportData data : reportData) {
            if (data.isReportWithTemplate()) {
                currentWorkbook = (XSSFWorkbook) WorkbookFactory.create(data.getTemplateURL().openStream());
                new ReportDataTemplateInjector(currentWorkbook, data).inject();
            } else {
                new ReportDataRawInjector(currentWorkbook, data).inject();
            }
        }
    }

    synchronized void writeToFileOutputStream(OutputStream fileOutputStream) throws IOException {
        currentWorkbook.write(fileOutputStream);
        fileOutputStream.close();
        currentWorkbook.close();
    }
}
