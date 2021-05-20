package org.greports.engine;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greports.utils.WorkbookUtils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class ReportEditor {

    private final XSSFWorkbook workbook;

    public ReportEditor(File file) throws IOException, InvalidFormatException {
        this((XSSFWorkbook) WorkbookFactory.create(file));
    }

    public ReportEditor(InputStream stream) throws IOException, InvalidFormatException {
        this((XSSFWorkbook) WorkbookFactory.create(stream));
    }

    private ReportEditor(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public ReportEditor setCellValue(String sheetName, Integer rowIndex, Integer columnIndex, Object value) {
        final Cell cell = this.getCell(sheetName, rowIndex, columnIndex);
        WorkbookUtils.setCellValue(cell, value);
        return this;
    }

    public ReportEditor setCellValue(String sheetName, Integer rowIndex, Integer columnIndex, Object value, String format) {
        this.setCellValue(sheetName, rowIndex, columnIndex, value);
        return this.setCellFormat(sheetName, rowIndex, columnIndex, format);
    }

    public ReportEditor setCellFormat(String sheetName, Integer rowIndex, Integer columnIndex, String format) {
        final Cell cell = this.getCell(sheetName, rowIndex, columnIndex);
        this.addCellFormat(cell, format);
        return this;
    }

    public ReportEditor setCellStyleProperty(String sheetName, Integer rowIndex, Integer columnIndex, String propertyName, Object propertyValue) {
        final Cell cell = this.getCell(sheetName, rowIndex, columnIndex);
        CellUtil.setCellStyleProperty(cell, propertyName, propertyValue);
        return this;
    }

    public ReportEditor setCellStyleProperties(String sheetName, Integer rowIndex, Integer columnIndex, CellStylesBuilder stylesBuilder) {
        final Cell cell = this.getCell(sheetName, rowIndex, columnIndex);
        final Map<String, Object> stylesMap = stylesBuilder.build();
        CellUtil.setCellStyleProperties(cell, stylesMap);
        return this;
    }

    public ReportEditor autosizeColumn(String sheetName, Integer columnIndex) {
        final Sheet sheet = this.getSheet(sheetName);
        sheet.autoSizeColumn(columnIndex);
        return this;
    }

    public int getLasRowNum(String sheetName) {
        return WorkbookUtils.getLastRowNum(this.workbook, sheetName);
    }

    public void write(OutputStream outputStream) throws IOException {
        this.workbook.write(outputStream);
        outputStream.close();
        this.workbook.close();
    }

    private void addCellFormat(Cell cell, String format) {
        CellStyle newStyle = workbook.createCellStyle();
        newStyle.cloneStyleFrom(cell.getCellStyle());
        newStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat(format));
        cell.setCellStyle(newStyle);
    }

    private Sheet getSheet(String sheetName) {
        return WorkbookUtils.getOrCreateSheet(workbook, sheetName);
    }

    private Cell getCell(String sheetName, Integer rowIndex, Integer columnIndex) {
        final Sheet sheet = getSheet(sheetName);
        final Row row = WorkbookUtils.getOrCreateRow(sheet, rowIndex);
        return WorkbookUtils.getOrCreateCell(row, columnIndex);
    }
}
