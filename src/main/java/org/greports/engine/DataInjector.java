package org.greports.engine;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greports.content.cell.SpecialDataCell;
import org.greports.content.row.SpecialDataRow;
import org.greports.services.LoggerService;
import org.greports.utils.WorkbookUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DataInjector {

    protected final XSSFWorkbook currentWorkbook;
    protected final ReportData reportData;
    protected final CreationHelper creationHelper;
    protected LoggerService loggerService;
    protected Map<String, XSSFCellStyle> formatsCache = new HashMap<>();

    protected abstract void inject();

    protected abstract void injectData(Sheet sheet);

    protected DataInjector(XSSFWorkbook currentWorkbook, ReportData reportData, boolean loggerEnabled) {
        this.currentWorkbook = currentWorkbook;
        this.reportData = reportData;
        this.creationHelper = this.currentWorkbook.getCreationHelper();
        this.loggerService = new LoggerService(this.getClass(), loggerEnabled);
    }

    protected CellReference getCellReferenceForTargetId(Row row, String id) {
        return new CellReference(row.getCell(reportData.getColumnIndexForId(id), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
    }

    protected void setCellFormat(Cell cell, String format) {
        if(format != null && !format.isEmpty()){
            XSSFCellStyle cellStyle;
            if(!formatsCache.containsKey(format)){
                cellStyle = currentWorkbook.createCellStyle();
                cellStyle.cloneStyleFrom(cell.getCellStyle());
                cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(format));
                formatsCache.put(format, cellStyle);
            } else {
                cellStyle = formatsCache.get(format);
            }
            cell.setCellStyle(cellStyle);
        }
    }

    protected String replaceFormulaIndexes(Row targetRow, String value) {
        for (Map.Entry<String, Integer> entry : reportData.getTargetIndexes().entrySet()) {
            value = value.replaceAll(entry.getKey(), this.getCellReferenceForTargetId(targetRow, entry.getKey()).formatAsString());
        }
        return value;
    }

    protected void adjustColumns(Sheet sheet) {
        for (Integer autoSizedColumn : reportData.getAutoSizedColumns()) {
            sheet.autoSizeColumn(autoSizedColumn + reportData.getConfiguration().getHorizontalOffset());
        }
    }

    protected void setGridlines(Sheet sheet) {
        sheet.setDisplayGridlines(reportData.getConfiguration().isShowGridlines());
    }

    protected void createSpecialRows(Sheet sheet) {
        final List<SpecialDataRow> specialRows = reportData.getSpecialRows();
        int countBottomRows = 0;
        for(SpecialDataRow specialRow : specialRows) {
            if(specialRow.getRowIndex() == Integer.MAX_VALUE) {
                specialRow.setRowIndex(
                    reportData.getConfiguration().getVerticalOffset() +
                    reportData.getDataStartRow() +
                    reportData.getRowsCount() +
                    countBottomRows++
                );
            } else {
                specialRow.setRowIndex(specialRow.getRowIndex() + reportData.getConfiguration().getVerticalOffset());
            }
            for(final SpecialDataCell specialCell : specialRow.getCells()) {
                Row row = sheet.getRow(specialRow.getRowIndex());
                if(row == null) {
                    row = sheet.createRow(specialRow.getRowIndex());
                }
                final int columnIndexForTarget = reportData.getColumnIndexForId(specialCell.getTargetId()) + reportData.getConfiguration().getHorizontalOffset();
                Cell cell = row.createCell(columnIndexForTarget);
                createColumnsToMerge(sheet, row, columnIndexForTarget, specialCell.getColumnWidth());
                final ValueType valueType = specialCell.getValueType();
                if(!ValueType.FORMULA.equals(valueType) &&
                        !ValueType.COLLECTED_FORMULA_VALUE.equals(valueType) &&
                        !ValueType.TEMPLATED_FORMULA.equals(valueType)) {
                    WorkbookUtils.setCellValue(cell, specialCell.getValue());
                } else {
                    String formulaString = specialCell.getValue().toString();
                    if(ValueType.FORMULA.equals(valueType)) {
                        createSpecialFormulaCell(sheet, specialCell, cell, formulaString);
                    } else {
                        Map<String, List<Integer>> extraData = (Map<String, List<Integer>>) specialCell.getExtraData();
                        if(extraData != null) {
                            for(final Map.Entry<String, List<Integer>> entry : extraData.entrySet()) {
                                String id = entry.getKey();
                                List<Integer> rowIndexes = entry.getValue();
                                List<String> cellReferences = new ArrayList<>();
                                for(final Integer rowIndex : rowIndexes) {
                                    CellReference cellReference = this.getCellReferenceForTargetId(
                                            sheet.getRow(reportData.getDataStartRow() + rowIndex + reportData.getConfiguration().getVerticalOffset()),
                                            specialCell.getTargetId()
                                    );
                                    cellReferences.add(cellReference.formatAsString() + ":" + cellReference.formatAsString());
                                }
                                String joinedReferences = String.join(",", cellReferences);
                                formulaString = formulaString.replaceAll(id, joinedReferences);
                                cell.setCellFormula(formulaString);
                            }
                        }
                    }
                }
                setCellFormat(cell, specialCell.getFormat());
            }
            if(specialRow.isStickyRow()) {
                sheet.createFreezePane(0, specialRow.getRowIndex() + 1, 0, specialRow.getRowIndex() + 1);
            }
        }
    }

    private void createSpecialFormulaCell(Sheet sheet, SpecialDataCell specialCell, Cell cell, String formulaString) {
        if(sheet.getLastRowNum() >= reportData.getDataStartRow()) {
            for (Map.Entry<String, Integer> entry : reportData.getTargetIndexes().entrySet()) {
                CellReference firstCellReference = this.getCellReferenceForTargetId(
                        sheet.getRow(reportData.getDataStartRow() + reportData.getConfiguration().getVerticalOffset()),
                        specialCell.getTargetId()
                );
                CellReference lastCellReference = this.getCellReferenceForTargetId(
                        sheet.getRow(reportData.getDataStartRow() + reportData.getRowsCount() + reportData.getConfiguration().getVerticalOffset() - 1),
                        specialCell.getTargetId()
                );
                formulaString = formulaString.replaceAll(entry.getKey(), firstCellReference.formatAsString() + ":" + lastCellReference.formatAsString());
            }
            cell.setCellFormula(formulaString);
        }
    }

    protected void createColumnsToMerge(final Sheet sheet, final Row row, final int cellIndex, final int columnWidth) {
        if(columnWidth > 1) {
            for (int i = 1; i < columnWidth; i++) {
                row.createCell(cellIndex + i, CellType.BLANK);
            }
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), cellIndex, cellIndex + columnWidth - 1));
        }
    }

}
