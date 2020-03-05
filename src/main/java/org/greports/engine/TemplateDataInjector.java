package org.greports.engine;

import org.greports.content.ReportData;
import org.greports.content.ReportHeader;
import org.greports.content.cell.DataCell;
import org.greports.content.row.DataRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;

import java.util.HashMap;
import java.util.Map;

public class TemplateDataInjector extends DataInjector {

    private final Map<Integer, XSSFCellStyle> _stylesCache = new HashMap<>();

    public TemplateDataInjector(XSSFWorkbook targetWorkbook, ReportData data, boolean loggerEnabled) {
        super(targetWorkbook, data, loggerEnabled);
    }

    @Override
    public void inject() {
        Sheet sheet = currentWorkbook.getSheet(reportData.getSheetName());
        injectData(sheet);
    }

    protected void injectData(Sheet sheet) {
        createHeader(sheet);
        createDataRows(sheet);
        reindexTablesRows(sheet);
//        evaluateFormulas();
    }

    private void createHeader(Sheet sheet) {
        if(reportData.isCreateHeader()){
            final ReportHeader header = reportData.getHeader();
            final Row targetHeaderRow = sheet.getRow(header.getRowIndex());
            for (int i = 0; i < header.getCells().size(); i++) {
                WorkbookUtils.setCellValue(targetHeaderRow.getCell(i), header.getCells().get(i).getValue());
            }
        }
    }

    private void cloneCell(Row sourceRow, Row targetRow, Object value, int cellIndex) {
        final Cell sourceRowCell = sourceRow.getCell(cellIndex);
        final Cell targetRowCell = targetRow.createCell(cellIndex);
        XSSFCellStyle cellStyle;
        if(_stylesCache.containsKey(cellIndex)){
            cellStyle = _stylesCache.get(cellIndex);
        } else {
            cellStyle = currentWorkbook.createCellStyle();
            cellStyle.cloneStyleFrom(sourceRowCell.getCellStyle());
            _stylesCache.put(cellIndex, cellStyle);
        }
        targetRowCell.setCellStyle(cellStyle);
        WorkbookUtils.setCellValue(targetRowCell, value);
    }

    private void createDataRows(Sheet sheet) {
        final Row sourceRow = sheet.getRow(reportData.getDataStartRow());
        for (int i = 0; i < reportData.getDataRows().size(); i++) {
            final Row targetRow = sheet.createRow(reportData.getDataStartRow() + i + 1);
            final DataRow dataRow = reportData.getDataRows().get(i);
            for (int cellIndex = 0; cellIndex < dataRow.getCells().size(); cellIndex++) {
                final DataCell dataCell = dataRow.getCells().get(cellIndex);
                cloneCell(sourceRow, targetRow, dataCell.getValue(), cellIndex);
            }
        }
        sheet.shiftRows(reportData.getDataStartRow() + 1, reportData.getDataStartRow() + reportData.getRowsCount() + 1, -1);
    }

//    private void reindexRow(final Sheet sheet) {
//        for (final XSSFTable table : currentWorkbook.getSheet(reportData.getSheetName()).getTables()) {
//            final Row lastDataRow = sheet.getRow(reportData.getDataStartRow() + reportData.getRowsCount() - 1);
//            table.setCellReferences(new AreaReference(
//                table.getCellReferences().getFirstCell(),
//                new CellReference(lastDataRow.getCell(table.getEndCellReference().getCol())),
//                SpreadsheetVersion.EXCEL2007
//            ));
//        }
//    }

    private void reindexTablesRows(final Sheet sheet) {
        for (final XSSFTable table : currentWorkbook.getSheet(reportData.getSheetName()).getTables()) {
            final Row lastDataRow = sheet.getRow(sheet.getLastRowNum());
            final CTTable ctTable = table.getCTTable();
            final AreaReference reference = new AreaReference(
                table.getStartCellReference(),
                new CellReference(lastDataRow.getCell(table.getEndColIndex()))
            );
            ctTable.setRef(reference.formatAsString());
        }
    }

    private void evaluateFormulas() {
        XSSFFormulaEvaluator.evaluateAllFormulaCells(currentWorkbook);
    }
}