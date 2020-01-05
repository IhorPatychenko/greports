package engine;


import content.ReportData;
import content.ReportHeader;
import content.column.ReportDataCell;
import content.row.ReportDataRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReportDataTemplateInjector extends ReportDataInjector {

    private final XSSFWorkbook sourceWorkbook;
    private final ReportData reportData;

    public ReportDataTemplateInjector(XSSFWorkbook sourceWorkbook, XSSFWorkbook targetWorkbook, ReportData data) {
        super(targetWorkbook);
        this.sourceWorkbook = sourceWorkbook;
        this.reportData = data;
    }

    @Override
    public void inject() {
        Sheet sourceSheet = sourceWorkbook.getSheet(reportData.getSheetName());
        injectData(sourceSheet);
    }

    protected void injectData(Sheet sourceSheet) {
        Sheet targetSheet = currentWorkbook.createSheet(sourceSheet.getSheetName());
        createHeader(sourceSheet, targetSheet);
        createDataRows(sourceSheet, targetSheet);
//        createSpecialRows(sheet);
//        addStripedRows(sheet);
//        addStyles(sheet);
//        adjustColumns(sheet);
    }

    private void createHeader(Sheet sourceSheet, Sheet targetSheet) {
        if(reportData.isShowHeader()){
            final ReportHeader header = reportData.getHeader();
            final Row sourceHeaderRow = sourceSheet.getRow(reportData.getHeaderStartRow());
            final Row targetHeaderRow = targetSheet.createRow(reportData.getHeaderStartRow());
            for (int i = 0; i < header.getCells().size(); i++) {
                cloneCell(sourceHeaderRow, targetHeaderRow, header.getCells().get(i).getTitle(), i);
            }
        }
    }

    private Cell cloneCell(Row sourceRow, Row targetRow, Object value, int cellIndex) {
        final Cell sourceHeaderRowCell = sourceRow.getCell(cellIndex);
        final Cell targetHeaderRowCell = targetRow.createCell(cellIndex);
        //targetHeaderRowCell.setCellStyle(sourceHeaderRowCell.getCellStyle());
        setCellValue(targetHeaderRowCell, value);
        return targetHeaderRowCell;
    }

    private void createDataRows(Sheet sourceSheet, Sheet targetSheet) {
        final Row sourceRow = sourceSheet.getRow(reportData.getDataStartRow());
        for (int i = 0; i < reportData.getRows().size(); i++) {
            final Row targetRow = targetSheet.createRow(reportData.getDataStartRow() + i);
            final ReportDataRow dataRow = reportData.getRows().get(i);
            for (int cellIndex = 0; cellIndex < dataRow.getCells().size(); cellIndex++) {
                final ReportDataCell reportDataCell = dataRow.getCells().get(cellIndex);
                final Cell cell = cloneCell(sourceRow, targetRow, reportDataCell.getValue(), cellIndex);
                setCellFormat(cell, sourceRow
                        .getCell(cellIndex)
                        .getCellStyle()
                        .getDataFormatString());
            }
        }
    }
}