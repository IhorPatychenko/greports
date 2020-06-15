package org.greports.engine;

import org.apache.log4j.Level;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.exceptions.ReportEngineRuntimeException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;


public class ReportTemplateNormalizer {

    public static boolean isNormalized(Class<?> clazz, String reportName, URL templateUrl) throws IOException, ReportEngineReflectionException {
        XSSFWorkbook workbook = new XSSFWorkbook(templateUrl.openStream());
        ReportConfiguration configuration = ReportConfigurationLoader.load(clazz, reportName);
        final ReportData reportData = getReportData(clazz, reportName);

        XSSFRow row = getRow(configuration, workbook);

        int columnsCount = reportData.getColumnsCount();
        for(int columnIndex = 0; columnIndex < columnsCount; columnIndex++) {
            XSSFCell cell = row.getCell(columnIndex);
            if(cell == null) {
                return false;
            }
        }
        return true;
    }

    public synchronized static void normalize(Class<?> clazz, String reportName, URL templateUrl) throws IOException, ReportEngineReflectionException {
        normalize(clazz, reportName, templateUrl, null);
    }

    public synchronized static void normalize(Class<?> clazz, String reportName, URL templateUrl, String savePath) throws IOException, ReportEngineReflectionException {
        XSSFWorkbook workbook = new XSSFWorkbook(templateUrl.openStream());
        ReportConfiguration configuration = ReportConfigurationLoader.load(clazz, reportName);
        final ReportData reportData = getReportData(clazz, reportName);

        XSSFRow row = getRow(configuration, workbook);

        int columnsCount = reportData.getColumnsCount();
        for(int columnIndex = 0; columnIndex < columnsCount; columnIndex++) {
            XSSFCell cell = row.getCell(columnIndex);
            if(cell == null) {
                cell = row.createCell(columnIndex);
            }
            if(cell.getRawValue() == null || "".equals(cell.getRawValue())) {
                cell.setCellValue(" ");
            }
        }

        if(savePath != null) {
            save(workbook, savePath);
        }
    }

    private static void save(XSSFWorkbook workbook, String savePath) throws IOException {
        workbook.write(new FileOutputStream(savePath));
        workbook.close();
    }

    private static ReportData getReportData(Class<?> clazz, String reportName) throws ReportEngineReflectionException {
        ReportDataParser dataParser = new ReportDataParser(true, Level.INFO);
        return dataParser.parse(clazz, reportName).getData();
    }

    private static XSSFRow getRow(ReportConfiguration configuration, XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.getSheet(configuration.getSheetName());
        if(sheet == null) {
            throw new ReportEngineRuntimeException("Sheet cannot be null. Check the sheet name.", ReportTemplateNormalizer.class);
        }
        XSSFRow row = sheet.getRow(configuration.getDataStartRowIndex());
        if(row == null) {
            throw new ReportEngineRuntimeException("Row cannot be null. Check dataStartRowIndex value.", ReportTemplateNormalizer.class);
        }
        return row;
    }

}
