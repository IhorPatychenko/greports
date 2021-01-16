package org.greports.engine;

import com.google.common.base.Stopwatch;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.greports.content.ReportHeader;
import org.greports.content.cell.DataCell;
import org.greports.content.cell.HeaderCell;
import org.greports.content.row.DataRow;
import org.greports.positioning.HorizontalRange;
import org.greports.positioning.VerticalRange;
import org.greports.styles.ReportStyle;
import org.greports.styles.interfaces.StripedRows;
import org.greports.utils.Pair;
import org.greports.utils.Utils;
import org.greports.utils.WorkbookUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

class RawDataInjector extends DataInjector {

    private final ReportData data;
    private Map<Pair<ReportStyle, String>, XSSFCellStyle> stylesCache = new HashedMap<>();

    public RawDataInjector(XSSFWorkbook currentWorkbook, ReportData reportData, boolean loggerEnabled) {
        super(currentWorkbook, reportData, loggerEnabled);
        this.data = reportData;
    }

    @Override
    public void inject() {
        Sheet sheet = getSheet(currentWorkbook, data);
        stylesCache = new HashedMap<>();
        formatsCache = new HashMap<>();
        injectData(sheet);
    }

    private Sheet getSheet(Workbook workbook, ReportData reportData) {
        if(reportData.getSheetName() == null) {
            return workbook.createSheet();
        } else {
            final Sheet sheet = workbook.getSheet(reportData.getSheetName());
            return sheet != null ? sheet : workbook.createSheet(reportData.getSheetName());
        }
    }

    protected void injectData(Sheet sheet) {
        loggerService.trace("Creating headers...");
        final Stopwatch headersStopwatch = Stopwatch.createStarted();
        createHeader(sheet);
        loggerService.trace("Headers created. Time: " + headersStopwatch.stop());

        loggerService.trace("Creating data rows...");
        final Stopwatch dataRowsStopwatch = Stopwatch.createStarted();
        createDataRows(sheet);
        loggerService.trace("Data rows created. Time: " + dataRowsStopwatch.stop());

        loggerService.trace("Creating special rows...");
        final Stopwatch specialRowsStopwatch = Stopwatch.createStarted();
        super.createSpecialRows(sheet);
        loggerService.trace("Special rows created. Time: " + specialRowsStopwatch.stop());

        loggerService.trace("Creating row's groups...");
        final Stopwatch rowsGroup = Stopwatch.createStarted();
        createRowsGroups(sheet);
        loggerService.trace("Row's groups created. Time: " + rowsGroup.stop());

        loggerService.trace("Creating row's groups...");
        final Stopwatch columnsGroup = Stopwatch.createStarted();
        createColumnsGroups(sheet);
        loggerService.trace("Column's groups created. Time: " + columnsGroup.stop());

        loggerService.trace("Adding striped row styles...");
        final Stopwatch stripedRowsStopwatch = Stopwatch.createStarted();
        addStripedRows(sheet);
        loggerService.trace("Striped row styles added. Time: " + stripedRowsStopwatch.stop());

        loggerService.trace("Adding styles...");
        final Stopwatch stylesStopwatch = Stopwatch.createStarted();
        addStyles(sheet);
        loggerService.trace("Styles added. Time: " + stylesStopwatch.stop());

        loggerService.trace("Adjusting columns...");
        final Stopwatch adjustColumnsStopwatch = Stopwatch.createStarted();
        super.adjustColumns(sheet);
        loggerService.trace("Columns adjusted. Time: " + adjustColumnsStopwatch.stop());

        setGridlines(sheet);
    }

    private void createHeader(Sheet sheet) {
        if(data.isCreateHeader()) {
            final ReportHeader header = data.getHeader();
            Row headerRow = sheet.getRow(header.getRowIndex() + data.getConfiguration().getVerticalOffset());
            if(headerRow == null) {
                headerRow = sheet.createRow(header.getRowIndex() + data.getConfiguration().getVerticalOffset());
            }
            int mergeCount = 0;
            for (int i = 0; i < header.getCells().size(); i++) {
                final HeaderCell headerCell = header.getCells().get(i);
                createHeaderCell(
                        sheet,
                        headerRow,
                        headerCell,
                        i + mergeCount + data.getConfiguration().getHorizontalOffset(),
                        headerCell.getColumnWidth()
                );
                if(headerCell.getColumnWidth() > 1) {
                    mergeCount += headerCell.getColumnWidth() - 1;
                }
            }

            if(header.isColumnFilter()) {
                sheet.setAutoFilter(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), 0, header.getCells().size() - 1));
            }

            if(header.isStickyHeader()) {
                sheet.createFreezePane(0, headerRow.getRowNum() + 1, 0, headerRow.getRowNum() + 1);
            }
        }
    }

    private void createDataRows(Sheet sheet) {
        // First create cells with data
        final Predicate<DataCell> dataCellsPredicate = (DataCell dataCell) -> !dataCell.getValueType().equals(ValueType.FORMULA) && !dataCell.getValueType().equals(ValueType.TEMPLATED_FORMULA);
        // After create cells with formulas to can evaluate them
        final Predicate<DataCell> formulaCellsPredicate = (DataCell dataCell) -> dataCell.getValueType().equals(ValueType.FORMULA);
        this.createCells(sheet, dataCellsPredicate);
        this.createCells(sheet, formulaCellsPredicate);
    }

    private void createCells(Sheet sheet, Predicate<DataCell> predicate) {
        for (int i = 0; i < data.getDataRows().size(); i++) {
            final DataRow dataRow = data.getDataRow(i);
            Row row = sheet.getRow(data.getDataStartRow() + data.getConfiguration().getVerticalOffset() + i);
            if(row == null) {
                row = sheet.createRow(data.getDataStartRow() + data.getConfiguration().getVerticalOffset() + i);
            }
            int mergedCellsCount = 0;
            for (int y = 0; y < dataRow.getCells().size(); y++) {
                final DataCell dataCell = dataRow.getCell(y);
                if(predicate.test(dataCell)) {
                    createCell(
                            sheet,
                            row,
                            dataCell,
                            dataCell.isPhysicalPosition()
                                    ? dataCell.getPosition().intValue()
                                    : mergedCellsCount + data.getConfiguration().getHorizontalOffset() + y
                    );
                    if(dataCell.getColumnWidth() > 1) {
                        mergedCellsCount += dataCell.getColumnWidth() - 1;
                    }
                }
            }
        }
    }

    private void createHeaderCell(final Sheet sheet, final Row row, final HeaderCell headerCell, final int cellIndex, final int columnWidth) {
        final Cell cell = row.createCell(cellIndex);
        super.createColumnsToMerge(sheet, row, cellIndex, columnWidth);
        WorkbookUtils.setCellValue(cell, headerCell.getValue());
    }

    private void createCell(Sheet sheet, Row row, DataCell dataCell, int columnIndex) {
        CellType cellType = CellType.BLANK;
        final ValueType valueType = dataCell.getValueType();
        if(!ValueType.FORMULA.equals(valueType) && !ValueType.TEMPLATED_FORMULA.equals(valueType)) {
            if(dataCell.getValue() instanceof Number) {
                cellType = CellType.NUMERIC;
            } else if(dataCell.getValue() instanceof String) {
                cellType = CellType.STRING;
            } else if(dataCell.getValue() instanceof Boolean) {
                cellType = CellType.BOOLEAN;
            }
            final Cell cell = row.createCell(columnIndex, cellType);
            WorkbookUtils.setCellValue(cell, dataCell.getValue());
            setCellFormat(cell, dataCell.getFormat());
        } else {
            cellType = CellType.FORMULA;
            final Cell cell = row.createCell(columnIndex, cellType);
            String formulaString = dataCell.getValue().toString();
            formulaString = replaceFormulaIndexes(row, formulaString);
            cell.setCellFormula(formulaString);
            setCellFormat(cell, dataCell.getFormat());
        }

        createColumnsToMerge(sheet, row, columnIndex, dataCell.getColumnWidth());
    }

    private void createRowsGroups(final Sheet sheet) {
        List<Pair<Integer, Integer>> groupedRows = data.getGroupedRows();
        for(final Pair<Integer, Integer> groupedRow : groupedRows) {
            int startGroup = data.getDataStartRow() + groupedRow.getLeft() + data.getConfiguration().getDataStartRowIndex();
            int endGroup = data.getDataStartRow() + groupedRow.getRight()  + data.getConfiguration().getDataStartRowIndex();
            sheet.groupRow(startGroup, endGroup);
            sheet.setRowGroupCollapsed(startGroup, data.isGroupedRowsDefaultCollapsed());
        }
    }

    private void createColumnsGroups(final Sheet sheet) {
        final List<Pair<Integer, Integer>> groupedColumns = data.getGroupedColumns();
        for(final Pair<Integer, Integer> groupedColumn : groupedColumns) {
            final int left = groupedColumn.getLeft() + data.getConfiguration().getHorizontalOffset();
            final int right = groupedColumn.getRight() + data.getConfiguration().getHorizontalOffset();
            sheet.groupColumn(left, right);
            sheet.setColumnGroupCollapsed(left, data.isGroupedColumnsDefaultCollapsed());
        }
    }

    private void addStripedRows(Sheet sheet) {
        final StripedRows.StripedRowsIndex stripedRowsIndex = data.getStyles().getStripedRowsIndex();
        final Color stripedRowsColor = data.getStyles().getStripedRowsColor();
        if(stripedRowsIndex != null && stripedRowsColor != null) {
            for (int i = stripedRowsIndex.getIndex() + data.getConfiguration().getVerticalOffset(); i <= sheet.getLastRowNum() + data.getConfiguration().getVerticalOffset(); i += 2) {
                final Row row = sheet.getRow(i);
                for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
                    final Cell cell = row.getCell(y);
                    final XSSFCellStyle cellStyle = currentWorkbook.createCellStyle();
                    cellStyle.cloneStyleFrom(cell.getCellStyle());
                    cellStyle.setFillForegroundColor(new XSSFColor(stripedRowsColor));
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(cellStyle);
                }
            }
        }
    }

    private void addStyles(Sheet sheet) {
        final List<ReportStyle> styles = data.getStyles().getReportStylesBuilder().getStyles();
        final short verticalOffset = data.getConfiguration().getVerticalOffset();
        final short horizontalOffset = data.getConfiguration().getHorizontalOffset();
        for (ReportStyle reportStyle : styles) {
            final VerticalRange verticalRange = reportStyle.getRange().getVerticalRange();
            checkRange(verticalRange, sheet);
            final HorizontalRange horizontalRange = reportStyle.getRange().getHorizontalRange();
            checkRange(horizontalRange, reportData);
            for (int i = verticalRange.getStart() + verticalOffset; i <= verticalRange.getEnd() + verticalOffset; i++) {
                final Row row = sheet.getRow(i);
                if(row != null){
                    for (int y = horizontalRange.getStart() + horizontalOffset; y <= horizontalRange.getEnd() + horizontalOffset; y++) {
                        cellApplyStyles(row.getCell(y), reportStyle);
                    }
                    if (reportStyle.getRowHeight() != null) {
                        row.setHeightInPoints(reportStyle.getRowHeight());
                    }
                }
            }
            if(reportStyle.getColumnWidth() != null) {
                for (int i = horizontalRange.getStart() + horizontalOffset; i <= horizontalRange.getEnd() + horizontalOffset; i++) {
                    sheet.setColumnWidth(i, reportStyle.getColumnWidth() * 256);
                }
            }
        }
    }

    private void checkRange(VerticalRange range, Sheet sheet) {
        if(Objects.isNull(range.getStart())) {
            range.setStart(sheet.getLastRowNum());
        } else if(range.getStart() < 0) {
            range.setStart(sheet.getLastRowNum() + range.getStart());
        }

        if(Objects.isNull(range.getEnd())) {
            range.setEnd(sheet.getLastRowNum());
        } else if(range.getEnd() < 0) {
            range.setEnd(sheet.getLastRowNum() + range.getEnd());
        }
    }

    private void checkRange(HorizontalRange range, ReportData reportData) {
        if(Objects.isNull(range.getStart())) {
            range.setStart(reportData.getColumnsCount() - 1);
        } else if(range.getStart() < 0) {
            range.setStart(reportData.getColumnsCount() + range.getStart() - 1);
        }

        if(Objects.isNull(range.getEnd())) {
            range.setEnd(reportData.getColumnsCount() - 1);
        } else if(range.getEnd() < 0) {
            range.setEnd(reportData.getColumnsCount() + range.getEnd() - 1);
        }
    }

    private void cellApplyStyles(Cell cell, ReportStyle style) {
        if(cell != null) {
            XSSFCellStyle cellStyle;
            final Pair<ReportStyle, String> styleKey = Pair.of(style, cell.getCellStyle().getDataFormatString());
            if(!stylesCache.containsKey(styleKey) || style.isClonePreviousStyle()) {
                cellStyle = currentWorkbook.createCellStyle();
                cellStyle.setDataFormat(cell.getCellStyle().getDataFormat());
                if(style.isClonePreviousStyle()) {
                    cellStyle.cloneStyleFrom(cell.getCellStyle());
                }

                // Borders
                cellApplyBorderStyles(style, cellStyle);

                // Colors
                cellApplyColorStyles(style, cellStyle);

                // Font
                cellApplyFontStyles(style, cellStyle);

                // Alignment
                cellApplyAlignmentStyles(style, cellStyle);

                // Other
                cellApplyOtherStyles(style, cellStyle);

                stylesCache.put(styleKey, cellStyle);
            } else {
                cellStyle = stylesCache.get(styleKey);
            }
            cell.setCellStyle(cellStyle);
        }
    }

    private void cellApplyOtherStyles(ReportStyle style, XSSFCellStyle cellStyle) {
        if(style.getHidden() != null) {
            cellStyle.setHidden(style.getHidden());
        }

        if(style.getIndentation() != null) {
            cellStyle.setIndention(style.getIndentation());
        }

        if(style.getLocked() != null) {
            cellStyle.setLocked(style.getLocked());
        }

        if(style.getQuotePrefixed() != null) {
            cellStyle.setQuotePrefixed(style.getQuotePrefixed());
        }

        if(style.getRotation() != null) {
            cellStyle.setRotation(style.getRotation());
        }

        if(style.getShrinkToFit() != null) {
            cellStyle.setShrinkToFit(style.getShrinkToFit());
        }

        if(style.getWrapText() != null) {
            cellStyle.setWrapText(style.getWrapText());
        }
    }

    private void cellApplyAlignmentStyles(ReportStyle style, XSSFCellStyle cellStyle) {
        if(style.getHorizontalAlignment() != null) {
            cellStyle.setAlignment(style.getHorizontalAlignment());
        }
        if(style.getVerticalAlignment() != null) {
            cellStyle.setVerticalAlignment(style.getVerticalAlignment());
        }
    }

    private void cellApplyFontStyles(ReportStyle style, XSSFCellStyle cellStyle) {
        if(Utils.anyNotNull(style.getFontSize(), style.getFontColor(), style.getBoldFont(), style.getItalicFont(), style.getUnderlineFont(), style.getStrikeoutFont())) {
            XSSFFont font = currentWorkbook.createFont();
            if(style.getFontSize() != null) {
                font.setFontHeightInPoints(style.getFontSize());
            }
            if(style.getFontColor() != null) {
                font.setColor(new XSSFColor(style.getFontColor()));
            }
            if(style.getBoldFont() != null) {
                font.setBold(style.getBoldFont());
            }
            if(style.getItalicFont() != null) {
                font.setItalic(style.getItalicFont());
            }
            if(style.getUnderlineFont() != null) {
                font.setUnderline(style.getUnderlineFont());
            }
            if(style.getStrikeoutFont() != null) {
                font.setStrikeout(style.getStrikeoutFont());
            }
            cellStyle.setFont(font);
        }
    }

    private void cellApplyColorStyles(ReportStyle style, XSSFCellStyle cellStyle) {
        if(style.getForegroundColor() != null) {
            cellStyle.setFillForegroundColor(new XSSFColor(style.getForegroundColor()));
            cellStyle.setFillPattern(style.getFillPattern());
        }

        if(style.getBorderColor() != null) {
            cellStyle.setBorderColor(XSSFCellBorder.BorderSide.TOP, new XSSFColor(style.getBorderColor()));
            cellStyle.setBorderColor(XSSFCellBorder.BorderSide.RIGHT, new XSSFColor(style.getBorderColor()));
            cellStyle.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM, new XSSFColor(style.getBorderColor()));
            cellStyle.setBorderColor(XSSFCellBorder.BorderSide.LEFT, new XSSFColor(style.getBorderColor()));
        }

        if(style.getLeftBorderColor() != null) {
            cellStyle.setLeftBorderColor(new XSSFColor(style.getLeftBorderColor()));
        }

        if(style.getRightBorderColor() != null) {
            cellStyle.setRightBorderColor(new XSSFColor(style.getRightBorderColor()));
        }

        if(style.getTopBorderColor() != null) {
            cellStyle.setTopBorderColor(new XSSFColor(style.getTopBorderColor()));
        }

        if(style.getBottomBorderColor() != null) {
            cellStyle.setBottomBorderColor(new XSSFColor(style.getBottomBorderColor()));
        }
    }

    private void cellApplyBorderStyles(ReportStyle style, XSSFCellStyle cellStyle) {
        if(style.getBorderBottom() != null) {
            cellStyle.setBorderBottom(style.getBorderBottom());
        }
        if(style.getBorderTop() != null) {
            cellStyle.setBorderTop(style.getBorderTop());
        }
        if(style.getBorderLeft() != null) {
            cellStyle.setBorderLeft(style.getBorderLeft());
        }
        if(style.getBorderRight() != null) {
            cellStyle.setBorderRight(style.getBorderRight());
        }
    }
}
