package engine;

import content.ReportData;
import content.ReportHeader;
import content.column.ReportDataCell;
import content.row.ReportDataRow;
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

public class ReportDataTemplateInjector extends ReportDataInjector {

    private final Map<Integer, XSSFCellStyle> _stylesCache = new HashMap<>();

    public ReportDataTemplateInjector(XSSFWorkbook targetWorkbook, ReportData data) {
        super(targetWorkbook, data);
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
            final Row targetHeaderRow = sheet.getRow(reportData.getHeaderRowIndex());
            for (int i = 0; i < header.getCells().size(); i++) {
                WorkbookUtils.setCellValue(targetHeaderRow.getCell(i), header.getCells().get(i).getTitle());
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
        for (int i = 0; i < reportData.getRows().size(); i++) {
            final Row targetRow = sheet.createRow(reportData.getDataStartRow() + i + 1);
            final ReportDataRow dataRow = reportData.getRows().get(i);
            for (int cellIndex = 0; cellIndex < dataRow.getCells().size(); cellIndex++) {
                final ReportDataCell reportDataCell = dataRow.getCells().get(cellIndex);
                cloneCell(sourceRow, targetRow, reportDataCell.getValue(), cellIndex);
            }
        }
        sheet.shiftRows(reportData.getDataStartRow() + 1, reportData.getDataStartRow() + reportData.getRowsCount() + 1, -1);
    }

//    private void reindexRow(final Sheet sheet) {
//        for (final XSSFTable table : currentWorkbook.getSheet(reportData.getSheetName()).getTables()) {
//            final Row lastDataRow = sheet.getRow(reportData.getDataStartRow() + reportData.getRowsCount() - 1);
//            table.setCellReferences(new AreaReference(
//                table.getCellReferences().getFirstCell(),
//                new CellReference(lastDataRow.getCell(lastDataRow.getLastCellNum() - 1)),
//                SpreadsheetVersion.EXCEL2007
//            ));
//        }
//    }

    private void reindexTablesRows(final Sheet sheet) {
        for (final XSSFTable table : currentWorkbook.getSheet(reportData.getSheetName()).getTables()) {
            final Row lastDataRow = sheet.getRow(reportData.getDataStartRow() + reportData.getRowsCount() - 1);
            final CTTable ctTable = table.getCTTable();
            final AreaReference reference = new AreaReference(
                table.getStartCellReference(),
                new CellReference(lastDataRow.getCell(lastDataRow.getLastCellNum() - 1))
            );
            ctTable.setRef(reference.formatAsString());
        }
    }

    private void evaluateFormulas() {
        XSSFFormulaEvaluator.evaluateAllFormulaCells(currentWorkbook);
    }
}