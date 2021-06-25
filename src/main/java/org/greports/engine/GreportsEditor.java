package org.greports.engine;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greports.utils.WorkbookUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class GreportsEditor {

    private final XSSFWorkbook workbook;

    /**
     * @param file input file
     * @throws IOException input/output exception
     * @throws InvalidFormatException this exception could be thrown when opening the workbook
     */
    public GreportsEditor(File file) throws IOException, InvalidFormatException {
        this((XSSFWorkbook) WorkbookFactory.create(file));
    }

    /**
     * @param stream file input stream
     * @throws IOException input/output exception
     * @throws InvalidFormatException this exception could be thrown when opening the workbook
     */
    public GreportsEditor(InputStream stream) throws IOException, InvalidFormatException {
        this((XSSFWorkbook) WorkbookFactory.create(stream));
    }

    private GreportsEditor(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    /**
     * @param sheetName a name of the sheet to change the cell's value
     * @param rowIndex a row index
     * @param columnIndex a column index (zero-based)
     * @param value new cell value
     * @return {@code ReportEditor}
     */
    public GreportsEditor setCellValue(String sheetName, Integer rowIndex, Integer columnIndex, Object value) {
        final Cell cell = this.getCell(sheetName, rowIndex, columnIndex);
        WorkbookUtils.setCellValue(cell, value);
        return this;
    }

    /**
     * @param sheetName a name of the sheet to change the cell's value
     * @param rowIndex a row index
     * @param columnIndex a column index (zero-based)
     * @param value new cell value
     * @param format new value format
     * @return {@code ReportEditor}
     */
    public GreportsEditor setCellValue(String sheetName, Integer rowIndex, Integer columnIndex, Object value, String format) {
        this.setCellValue(sheetName, rowIndex, columnIndex, value);
        return this.setCellFormat(sheetName, rowIndex, columnIndex, format);
    }

    /**
     * @param sheetName a name of the sheet to change the cell's value
     * @param rowIndex a row index
     * @param columnIndex a column index (zero-based)
     * @param format new value format
     * @return {@code ReportEditor}
     */
    public GreportsEditor setCellFormat(String sheetName, Integer rowIndex, Integer columnIndex, String format) {
        final Cell cell = this.getCell(sheetName, rowIndex, columnIndex);
        this.addCellFormat(cell, format);
        return this;
    }

    /**
     * @param sheetName a name of the sheet to change the cell's style
     * @param rowIndex a row index
     * @param columnIndex a column index (zero-based)
     * @param propertyName a name of style property to be changed
     * @param propertyValue a new style property value
     * @return {@code ReportEditor}
     */
    public GreportsEditor setCellStyleProperty(String sheetName, Integer rowIndex, Integer columnIndex, String propertyName, Object propertyValue) {
        final Cell cell = this.getCell(sheetName, rowIndex, columnIndex);
        CellUtil.setCellStyleProperty(cell, propertyName, propertyValue);
        return this;
    }

    /**
     * @param sheetName a name of the sheet to change the cell's style
     * @param rowIndex a row index
     * @param columnIndex a column index (zero-based)
     * @param stylesBuilder styles builder
     * @return {@code ReportEditor}
     */
    public GreportsEditor setCellStyleProperties(String sheetName, Integer rowIndex, Integer columnIndex, CellStylesBuilder stylesBuilder) {
        final Cell cell = this.getCell(sheetName, rowIndex, columnIndex);
        final Map<String, Object> stylesMap = stylesBuilder.build();
        CellUtil.setCellStyleProperties(cell, stylesMap);
        return this;
    }

    /**
     *
     * @param sheetName a sheet name
     * @param columnIndex column index (zero-based)
     * @return {@code ReportEditor}
     */
    public GreportsEditor autosizeColumn(String sheetName, Integer columnIndex) {
        final Sheet sheet = this.getSheet(sheetName);
        sheet.autoSizeColumn(columnIndex);
        return this;
    }

    public int getLasRowNum(String sheetName) {
        return WorkbookUtils.getLastRowNum(this.workbook, sheetName);
    }

    /**
     * Writes a workbook content to the output stream received by parameter.
     * If the workbook was opened via {@code File} constructor, then the output stream should be pointing to
     * another file, if not {@code IOException} will be thrown, because the file is not closed.
     * If the workbook was opened via {@code InputStream} constructor, then it is possible to write to the same file.
     *
     * @param outputStream output stream where to save the workbook's data
     * @throws IOException input/output exception could be thrown when trying to write to the stream
     */
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
