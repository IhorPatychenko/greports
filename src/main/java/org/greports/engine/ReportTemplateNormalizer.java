package org.greports.engine;

import org.apache.log4j.Level;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.exceptions.ReportEngineRuntimeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;


public class ReportTemplateNormalizer {

    private XSSFWorkbook workbook;

    public ReportTemplateNormalizer(URL templateUrl) throws IOException {
        this.workbook = new XSSFWorkbook(templateUrl.openStream());
    }

    public ReportTemplateNormalizer(File file) throws IOException {
        this.workbook = new XSSFWorkbook(new FileInputStream(file));
    }

    public ReportTemplateNormalizer reset() {
        workbook = null;
        return this;
    }

    public boolean isNormalized(Class<?> clazz, String reportName) throws ReportEngineReflectionException {
        ReportConfiguration configuration = ReportConfigurationLoader.load(clazz, reportName);
        final ReportData reportData = getReportData(clazz, reportName);

        XSSFRow row = getRow(configuration);

        int columnsCount = reportData.getColumnsCount();
        for(int columnIndex = 0; columnIndex < columnsCount; columnIndex++) {
            XSSFCell cell = row.getCell(columnIndex);
            if(cell == null) {
                return false;
            }
        }
        return true;
    }

    public synchronized ReportTemplateNormalizer normalize(Class<?> clazz, String reportName) throws ReportEngineReflectionException {
        ReportConfiguration configuration = ReportConfigurationLoader.load(clazz, reportName);
        final ReportData reportData = getReportData(clazz, reportName);
        XSSFRow row = getRow(configuration);
        int columnsCount = reportData.getColumnsCount();
        normalizeCells(row, columnsCount);
        return this;
    }

    public synchronized ReportTemplateNormalizer normalize(String sheetName, int dataStartRowIndex, int lastColumnIndex) {
        XSSFSheet sheet = getSheet(sheetName);
        XSSFRow row = getRow(sheet, dataStartRowIndex);
        normalizeCells(row, lastColumnIndex);
        return this;
    }

    private void normalizeCells(XSSFRow row, int lastColumnIndex) {
        for(int columnIndex = 0; columnIndex < lastColumnIndex; columnIndex++) {
            XSSFCell cell = row.getCell(columnIndex);
            if(cell == null) {
                row.createCell(columnIndex);
            }
        }
    }

    public synchronized ReportTemplateNormalizer save(String filePath) throws IOException {
        return this.save(new FileOutputStream(filePath));
    }

    public synchronized ReportTemplateNormalizer save(File file) throws IOException {
        return this.save(new FileOutputStream(file));
    }

    public synchronized ReportTemplateNormalizer save(OutputStream outputStream) throws IOException {
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
        return this;
    }

    private ReportData getReportData(Class<?> clazz, String reportName) throws ReportEngineReflectionException {
        ReportDataParser dataParser = new ReportDataParser(true, Level.INFO);
        return dataParser.parse(clazz, reportName).getData();
    }

    private XSSFSheet getSheet(ReportConfiguration configuration) {
        return getSheet(configuration.getSheetName());
    }

    private XSSFSheet getSheet(String sheetName) {
        XSSFSheet sheet = workbook.getSheet(sheetName);
        if(sheet == null) {
            throw new ReportEngineRuntimeException("Sheet cannot be null. Check the sheet name.", ReportTemplateNormalizer.class);
        }
        return sheet;
    }

    private XSSFRow getRow(ReportConfiguration configuration) {
        XSSFSheet sheet = getSheet(configuration);
        return getRow(sheet, configuration.getDataStartRowIndex());
    }

    private XSSFRow getRow(XSSFSheet sheet, int rowIndex) {
        XSSFRow row = sheet.getRow(rowIndex);
        if(row == null) {
            throw new ReportEngineRuntimeException("Row cannot be null. Check dataStartRowIndex value.", ReportTemplateNormalizer.class);
        }
        return row;
    }

}
