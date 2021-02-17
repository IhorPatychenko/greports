package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
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
import java.util.Arrays;
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

    protected DataInjector(XSSFWorkbook currentWorkbook, ReportData reportData, boolean loggerEnabled, Level level) {
        this.currentWorkbook = currentWorkbook;
        this.reportData = reportData;
        this.creationHelper = this.currentWorkbook.getCreationHelper();
        this.loggerService = new LoggerService(this.getClass(), loggerEnabled, level);
    }

    protected CellReference getCellReferenceForTargetId(Row row, String id) {
        return new CellReference(row.getCell(reportData.getColumnIndexForId(id), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
    }

    private void setCellComment(Sheet sheet, Cell cell, SpecialDataCell specialCell) {
        if(!StringUtils.EMPTY.equals(specialCell.getComment())) {
            final Drawing<?> drawingPatriarch = sheet.createDrawingPatriarch();
            final ClientAnchor clientAnchor = creationHelper.createClientAnchor();
            clientAnchor.setCol1(cell.getColumnIndex());
            clientAnchor.setCol2(cell.getColumnIndex() + specialCell.getCommentWidth());
            clientAnchor.setRow1(cell.getRowIndex());
            clientAnchor.setRow2(cell.getRowIndex() + specialCell.getCommentHeight());
            final Comment cellComment = drawingPatriarch.createCellComment(clientAnchor);
            cellComment.setString(creationHelper.createRichTextString(specialCell.getComment()));
        }
    }

    protected void setCellFormat(Cell cell, String format) {
        if(!StringUtils.isEmpty(format)){
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
        final List<Integer> autoSizedColumns = reportData.getAutoSizedColumns();

        loggerService.trace("Adjusting columns...", !autoSizedColumns.isEmpty());
        final Stopwatch adjustColumnsStopwatch = Stopwatch.createStarted();

        for (Integer autoSizedColumn : autoSizedColumns) {
            sheet.autoSizeColumn(autoSizedColumn + reportData.getConfiguration().getHorizontalOffset());
        }

        loggerService.trace("Columns adjusted. Time: " + adjustColumnsStopwatch.stop(), !autoSizedColumns.isEmpty());
    }

    protected void setGridlines(Sheet sheet) {
        sheet.setDisplayGridlines(reportData.getConfiguration().isShowGridlines());
    }

    protected void createSpecialRows(Sheet sheet) {
        final List<SpecialDataRow> specialRows = reportData.getSpecialRows();
        Integer countBottomRows = 0;
        loggerService.trace("Creating special rows...", !specialRows.isEmpty());
        final Stopwatch specialRowsStopwatch = Stopwatch.createStarted();
        for(SpecialDataRow specialRow : specialRows) {
            countBottomRows = specialRowSetRowIndex(countBottomRows, specialRow);
            for(final SpecialDataCell specialCell : specialRow.getCells()) {
                final ValueType valueType = specialCell.getValueType();
                if(ValueType.TEMPLATED_FORMULA.equals(valueType)) {
                    continue;
                }
                Row row = getOrCreateRow(sheet, specialRow.getRowIndex());
                final int columnIndexForTarget = reportData.getColumnIndexForId(specialCell.getTargetId()) + reportData.getConfiguration().getHorizontalOffset();
                Cell cell = row.createCell(columnIndexForTarget);
                createColumnsToMerge(sheet, row, columnIndexForTarget, specialCell.getColumnWidth());

                if(!Arrays.asList(ValueType.FORMULA, ValueType.COLLECTED_FORMULA_VALUE, ValueType.TEMPLATED_FORMULA).contains(valueType)) {
                    WorkbookUtils.setCellValue(cell, specialCell.getValue());
                } else {
                    String formulaString = specialCell.getValue().toString();
                    if(ValueType.FORMULA.equals(valueType)) {
                        createSpecialFormulaCell(sheet, specialCell, cell, formulaString);
                    } else {
                        createCollectedFormulaValueCell(sheet, specialCell, cell, formulaString);
                    }
                }
                setCellComment(sheet, cell, specialCell);
                setCellFormat(cell, specialCell.getFormat());
            }
            checkIfStickyRow(sheet, specialRow);
        }
        loggerService.trace("Special rows created. Time: " + specialRowsStopwatch.stop(), !specialRows.isEmpty());
    }

    protected Row getOrCreateRow(Sheet sheet, Integer rowIndex) {
        final Row row = sheet.getRow(rowIndex);
        return row != null ? row : sheet.createRow(rowIndex);
    }

    private void createCollectedFormulaValueCell(Sheet sheet, SpecialDataCell specialCell, Cell cell, String formulaString) {
        Map<String, List<Integer>> valuesById = (Map<String, List<Integer>>) specialCell.getValuesById();
        if(valuesById != null) {
            for(final Map.Entry<String, List<Integer>> entry : valuesById.entrySet()) {
                String id = entry.getKey();
                List<Integer> rowIndexes = entry.getValue();
                List<String> cellReferences = new ArrayList<>();
                for(final Integer rowIndex : rowIndexes) {
                    CellReference cellReference = this.getCellReferenceForTargetId(
                            getOrCreateRow(sheet, reportData.getDataRealStartRow() + rowIndex),
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

    private void checkIfStickyRow(Sheet sheet, SpecialDataRow specialRow) {
        if(specialRow.isStickyRow()) {
            sheet.createFreezePane(0, specialRow.getRowIndex() + 1, 0, specialRow.getRowIndex() + 1);
        }
    }

    private Integer specialRowSetRowIndex(Integer countBottomRows, SpecialDataRow specialRow) {
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
        return countBottomRows;
    }

    private void createSpecialFormulaCell(Sheet sheet, SpecialDataCell specialCell, Cell cell, String formulaString) {
        if(sheet.getLastRowNum() >= reportData.getDataStartRow()) {
            for (Map.Entry<String, Integer> entry : reportData.getTargetIndexes().entrySet()) {
                CellReference firstCellReference = this.getCellReferenceForTargetId(
                        getOrCreateRow(sheet, reportData.getDataRealStartRow()),
                        specialCell.getTargetId()
                );
                CellReference lastCellReference = this.getCellReferenceForTargetId(
                        getOrCreateRow(sheet,reportData.getDataRealStartRow() + reportData.getRowsCount() - 1),
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
