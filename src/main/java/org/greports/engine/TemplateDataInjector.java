package org.greports.engine;

import org.apache.logging.log4j.Level;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greports.content.ReportHeader;
import org.greports.content.cell.DataCell;
import org.greports.content.row.DataRow;
import org.greports.utils.WorkbookUtils;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;


public class TemplateDataInjector extends DataInjector {

    public TemplateDataInjector(XSSFWorkbook targetWorkbook, ReportData data, boolean loggerEnabled, Level level) {
        super(targetWorkbook, data, loggerEnabled, level);
    }

    @Override
    public void inject() {
        Sheet sheet = currentWorkbook.getSheet(reportData.getSheetName());
        injectData(sheet);
    }

    protected void injectData(Sheet sheet) {
        createHeader(sheet);
        createDataRows(sheet);
        super.createSpecialRows(sheet);
        reindexTablesRows(sheet);
        super.adjustColumns(sheet);
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

    private void cloneCell(Sheet sheet, Row sourceRow, Row targetRow, DataCell dataCell, int cellIndex) {
        if(!ValueType.IGNORED_VALUE.equals(dataCell.getValueType())) {
            final Cell sourceRowCell = sourceRow.getCell(cellIndex);
            final Cell targetRowCell = targetRow.createCell(cellIndex);
            targetRowCell.setCellStyle(sourceRowCell.getCellStyle());
            Object value = dataCell.getValue();
            if(ValueType.FORMULA.equals(dataCell.getValueType())) {
                value = replaceFormulaIndexes(sourceRow, value.toString());
            } else if(ValueType.TEMPLATED_FORMULA.equals(dataCell.getValueType())) {
                value = copyFormula(sheet, sourceRowCell.getCellFormula(), targetRow.getRowNum() - sourceRow.getRowNum());
            }
            WorkbookUtils.setCellValue(targetRowCell, value, dataCell.getValueType());
        }
    }

    private String copyFormula(Sheet sheet, String formula, int rowdiff){
        EvaluationWorkbook evaluationWorkbook = XSSFEvaluationWorkbook.create(currentWorkbook);
        Ptg[] ptgs = FormulaParser.parse(formula,
            (FormulaParsingWorkbook) evaluationWorkbook,
            FormulaType.CELL,
            sheet.getWorkbook().getSheetIndex(sheet)
        );

        for(Ptg ptg : ptgs) {
            changeFormulaRowIndex(ptg, rowdiff);
        }

        formula = FormulaRenderer.toFormulaString((FormulaRenderingWorkbook)evaluationWorkbook, ptgs);
        return formula;
    }

    private void changeFormulaRowIndex(Ptg ptg, int rowdiff) {
        if(ptg instanceof RefPtgBase) { // base class for cell references
            RefPtgBase ref = (RefPtgBase) ptg;
            if(ref.isRowRelative()) {
                ref.setRow(ref.getRow() + rowdiff);
            }
        } else if(ptg instanceof AreaPtgBase) { // base class for range references
            AreaPtgBase ref = (AreaPtgBase) ptg;
            if(ref.isFirstRowRelative()) {
                ref.setFirstRow(ref.getFirstRow() + rowdiff);
            }
            if(ref.isLastRowRelative()) {
                ref.setLastRow(ref.getLastRow() + rowdiff);
            }
        }
    }

    private void createDataRows(Sheet sheet) {
        final Row sourceRow = sheet.getRow(reportData.getDataStartRow());
        for (int i = 0; i < reportData.getDataRows().size(); i++) {
            final int targetRowIndex = reportData.getDataStartRow() + i + 1;
            Row targetRow = sheet.getRow(targetRowIndex);
            if(targetRow == null) {
                targetRow = sheet.createRow(targetRowIndex);
            }
            final DataRow dataRow = reportData.getDataRows().get(i);
            for (int cellIndex = 0; cellIndex < dataRow.getCells().size(); cellIndex++) {
                cloneCell(sheet, sourceRow, targetRow, dataRow.getCells().get(cellIndex), cellIndex);
            }
        }
        sheet.shiftRows(reportData.getDataStartRow() + 1, reportData.getDataStartRow() + reportData.getRowsCount() + 1, -1);
    }

    private void reindexTablesRows(final Sheet sheet) {
        for (final XSSFTable table : currentWorkbook.getSheet(reportData.getSheetName()).getTables()) {
            final Row lastDataRow = sheet.getRow(reportData.getDataStartRow() + reportData.getRowsCount() - 1);
            final CTTable ctTable = table.getCTTable();
            final AreaReference reference = new AreaReference(
                table.getStartCellReference(),
                new CellReference(lastDataRow.getCell(table.getEndColIndex()))
            );
            ctTable.setRef(reference.formatAsString());
        }
    }
}