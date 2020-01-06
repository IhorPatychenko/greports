package engine;

import content.ReportData;
import content.ReportHeader;
import content.column.ReportDataCell;
import content.row.ReportDataRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.Pair;

import java.util.HashMap;
import java.util.Map;

public class ReportDataTemplateInjector extends ReportDataInjector {

    private enum CellType {
        HEADER, DATA
    }

    private final XSSFWorkbook sourceWorkbook;
    private final Map<Pair<Integer, CellType>, XSSFCellStyle> _stylesCache = new HashMap<>();

    public ReportDataTemplateInjector(XSSFWorkbook sourceWorkbook, XSSFWorkbook targetWorkbook, ReportData data) {
        super(targetWorkbook, data);
        this.sourceWorkbook = sourceWorkbook;
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
    }

    private void createHeader(Sheet sourceSheet, Sheet targetSheet) {
        if(reportData.isShowHeader()){
            final ReportHeader header = reportData.getHeader();
            final Row sourceHeaderRow = sourceSheet.getRow(reportData.getHeaderStartRow());
            final Row targetHeaderRow = targetSheet.createRow(reportData.getHeaderStartRow());
            for (int i = 0; i < header.getCells().size(); i++) {
                cloneCell(sourceHeaderRow, targetHeaderRow, header.getCells().get(i).getTitle(), i, CellType.HEADER);
            }
        }
    }

    private void cloneCell(Row sourceRow, Row targetRow, Object value, int cellIndex, CellType cellType) {
        final Cell sourceRowCell = sourceRow.getCell(cellIndex);
        final Cell targetRowCell = targetRow.createCell(cellIndex);
        Pair<Integer, CellType> cellTypePair = Pair.of(cellIndex, cellType);
        XSSFCellStyle cellStyle;
        if(_stylesCache.containsKey(cellTypePair)){
            cellStyle = _stylesCache.get(cellTypePair);
        } else {
            cellStyle = currentWorkbook.createCellStyle();
            cellStyle.cloneStyleFrom(sourceRowCell.getCellStyle());
            _stylesCache.put(cellTypePair, cellStyle);
        }
        targetRowCell.setCellStyle(cellStyle);
        setCellValue(targetRowCell, value);
    }

    private void createDataRows(Sheet sourceSheet, Sheet targetSheet) {
        final Row sourceRow = sourceSheet.getRow(reportData.getDataStartRow());
        for (int i = 0; i < reportData.getRows().size(); i++) {
            final Row targetRow = targetSheet.createRow(reportData.getDataStartRow() + i);
            final ReportDataRow dataRow = reportData.getRows().get(i);
            for (int cellIndex = 0; cellIndex < dataRow.getCells().size(); cellIndex++) {
                final ReportDataCell reportDataCell = dataRow.getCells().get(cellIndex);
                cloneCell(sourceRow, targetRow, reportDataCell.getValue(), cellIndex, CellType.DATA);
            }
        }
    }
}