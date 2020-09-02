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
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.greports.content.ReportHeader;
import org.greports.content.cell.DataCell;
import org.greports.content.cell.HeaderCell;
import org.greports.content.cell.SpecialDataCell;
import org.greports.content.row.DataRow;
import org.greports.content.row.SpecialDataRow;
import org.greports.positioning.HorizontalRange;
import org.greports.positioning.Position;
import org.greports.positioning.RectangleRange;
import org.greports.positioning.VerticalRange;
import org.greports.styles.HorizontalRangedStyle;
import org.greports.styles.PositionedStyle;
import org.greports.styles.RectangleRangedStyle;
import org.greports.styles.ReportStyle;
import org.greports.styles.VerticalRangedStyle;
import org.greports.styles.interfaces.StripedRows;
import org.greports.styles.stylesbuilders.AbstractReportStylesBuilder;
import org.greports.styles.stylesbuilders.HorizontalRangedStylesBuilder;
import org.greports.styles.stylesbuilders.PositionedStylesBuilder;
import org.greports.styles.stylesbuilders.RectangleRangedStylesBuilder;
import org.greports.styles.stylesbuilders.VerticalRangedStylesBuilder;
import org.greports.utils.Pair;
import org.greports.utils.Utils;
import org.greports.utils.WorkbookUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class RawDataInjector extends DataInjector {

    private final ReportData data;
    private Map<Pair<ReportStyle<?>, String>, XSSFCellStyle> stylesCache = new HashedMap<>();

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
        createSpecialRows(sheet);
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
    }

    private void createHeader(Sheet sheet) {
        if(data.isCreateHeader()) {
            final ReportHeader header = data.getHeader();
            final Row headerRow = sheet.createRow(header.getRowIndex());
            int mergeCount = 0;
            for (int i = 0; i < header.getCells().size(); i++) {
                final HeaderCell headerCell = header.getCells().get(i);
                createHeaderCell(sheet, headerRow, headerCell, i + mergeCount, headerCell.getColumnWidth());
                if(headerCell.getColumnWidth() > 1) {
                    mergeCount += headerCell.getColumnWidth() - 1;
                }
            }

            if(header.isColumnFilter()) {
                sheet.setAutoFilter(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), 0, header.getCells().size() - 1));
            }
        }
    }

    private void createDataRows(Sheet sheet) {
        // First create cells with data
        createDataCells(sheet);
        // After create cells with formulas to can evaluate them
        createFormulaCells(sheet);
    }

    private void createDataCells(Sheet sheet) {
        for (int i = 0; i < data.getDataRows().size(); i++) {
            final DataRow dataRow = data.getDataRow(i);
            final Row row = sheet.createRow(data.getDataStartRow() + i);
            int mergedCellsCount = 0;
            for (int y = 0; y < dataRow.getCells().size(); y++) {
                final DataCell dataCell = dataRow.getCell(y);
                if(!dataCell.getValueType().equals(ValueType.FORMULA) && !dataCell.getValueType().equals(ValueType.TEMPLATED_FORMULA)) {
                    createCell(sheet, row, dataCell, dataCell.isPhysicalPosition() ? dataCell.getPosition().intValue() : y + mergedCellsCount);
                    if(dataCell.getColumnWidth() > 1) {
                        mergedCellsCount += dataCell.getColumnWidth() - 1;
                    }
                }
            }
        }
    }

    private void createFormulaCells(Sheet sheet) {
        for (int i = 0; i < data.getDataRows().size(); i++) {
            final DataRow dataRow = data.getDataRow(i);
            final Row row = sheet.getRow(data.getDataStartRow() + i);
            int mergedCellsCount = 0;
            for (int y = 0; y < dataRow.getCells().size(); y++) {
                final DataCell dataCell = dataRow.getCell(y);
                if(dataCell.getValueType().equals(ValueType.FORMULA)) {
                    createCell(sheet, row, dataCell, dataCell.isPhysicalPosition() ? dataCell.getPosition().intValue() : y + mergedCellsCount);
                    if(dataCell.getColumnWidth() > 1) {
                        mergedCellsCount += dataCell.getColumnWidth() - 1;
                    }
                }
            }
        }
    }

    private void createHeaderCell(final Sheet sheet, final Row row, final HeaderCell headerCell, final int cellIndex, final int columnWidth) {
        final Cell cell = row.createCell(cellIndex);
        createColumnsToMerge(sheet, row, cellIndex, columnWidth);
        WorkbookUtils.setCellValue(cell, headerCell.getValue());
    }

    private void createColumnsToMerge(final Sheet sheet, final Row row, final int cellIndex, final int columnWidth) {
        if(columnWidth > 1) {
            for (int i = 1; i < columnWidth; i++) {
                row.createCell(cellIndex + i, CellType.BLANK);
            }
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), cellIndex, cellIndex + columnWidth - 1));
        }
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

    private void createSpecialRows(Sheet sheet) {
        final List<SpecialDataRow> specialRows = data.getSpecialRows();
        for (int i = 0; i < specialRows.size(); i++) {
            SpecialDataRow specialRow = specialRows.get(i);
            if(specialRow.getRowIndex() == Integer.MAX_VALUE) {
                specialRow.setRowIndex(data.getDataStartRow() + data.getRowsCount() + i);
            }
            for (final SpecialDataCell specialCell : specialRow.getCells()) {
                Row row = sheet.getRow(specialRow.getRowIndex());
                if(row == null) {
                    row = sheet.createRow(specialRow.getRowIndex());
                }
                final Integer columnIndexForTarget = data.getColumnIndexForId(specialCell.getTargetId());
                Cell cell = row.createCell(columnIndexForTarget);
                createColumnsToMerge(sheet, row, columnIndexForTarget, specialCell.getColumnWidth());
                final ValueType valueType = specialCell.getValueType();
                if(!ValueType.FORMULA.equals(valueType) &&
                    !ValueType.COLLECTED_FORMULA_VALUE.equals(valueType) &&
                    !ValueType.TEMPLATED_FORMULA.equals(valueType)) {
                    WorkbookUtils.setCellValue(cell, specialCell.getValue());
                } else {
                    String formulaString = specialCell.getValue().toString();
                    if(ValueType.FORMULA.equals(valueType)){
                        if(sheet.getLastRowNum() > data.getDataStartRow()) {
                            for (Map.Entry<String, Integer> entry : data.getTargetIndexes().entrySet()) {
                                CellReference firstCellReference = super.getCellReferenceForTargetId(sheet.getRow(data.getDataStartRow()), specialCell.getTargetId());
                                CellReference lastCellReference = super.getCellReferenceForTargetId(sheet.getRow(data.getDataStartRow() + data.getRowsCount() - 1), specialCell.getTargetId());
                                formulaString = formulaString.replaceAll(entry.getKey(), firstCellReference.formatAsString() + ":" + lastCellReference.formatAsString());
                            }
                        }
                        if(sheet.getLastRowNum() > data.getDataStartRow()) {
                            cell.setCellFormula(formulaString);
                        }
                    } else {
                        Map<String, List<Integer>> extraData = (Map<String, List<Integer>>) specialCell.getExtraData();
                        if(extraData != null) {
                            for(final Map.Entry<String, List<Integer>> entry : extraData.entrySet()) {
                                String id = entry.getKey();
                                List<Integer> rowIndexes = entry.getValue();
                                List<String> cellReferences = new ArrayList<>();
                                for(final Integer rowIndex : rowIndexes) {
                                    CellReference cellReference = super.getCellReferenceForTargetId(sheet.getRow(data.getDataStartRow() + rowIndex), specialCell.getTargetId());
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
        }
    }

    private void createRowsGroups(final Sheet sheet) {
        List<Pair<Integer, Integer>> groupedRows = data.getGroupedRows();
        for(final Pair<Integer, Integer> groupedRow : groupedRows) {
            int startGroup = data.getDataStartRow() + groupedRow.getLeft();
            int endGroup = data.getDataStartRow() + groupedRow.getRight();
            sheet.groupRow(startGroup, endGroup);
            sheet.setRowGroupCollapsed(startGroup, data.isGroupedRowsDefaultCollapsed());
        }
    }

    private void createColumnsGroups(final Sheet sheet) {
        final List<Pair<Integer, Integer>> groupedColumns = data.getGroupedColumns();
        for(final Pair<Integer, Integer> groupedColumn : groupedColumns) {
            sheet.groupColumn(groupedColumn.getLeft(), groupedColumn.getRight());
            sheet.setColumnGroupCollapsed(groupedColumn.getLeft(), data.isGroupedColumnsDefaultCollapsed());
        }
    }

    private void addStripedRows(Sheet sheet) {
        final StripedRows.StripedRowsIndex stripedRowsIndex = data.getStyles().getStripedRowsIndex();
        final Color stripedRowsColor = data.getStyles().getStripedRowsColor();
        if(stripedRowsIndex != null && stripedRowsColor != null) {
            for (int i = stripedRowsIndex.getIndex(); i <= sheet.getLastRowNum(); i += 2) {
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
        for (AbstractReportStylesBuilder.StylePriority priority : AbstractReportStylesBuilder.StylePriority.values()) {
            if(data.getStyles().getRowStyles() != null && priority.equals(data.getStyles().getRowStyles().getPriority())) {
                applyRowStyles(sheet, data.getStyles().getRowStyles());
            }
            if(data.getStyles().getColumnStyles() != null && priority.equals(data.getStyles().getColumnStyles().getPriority())) {
                applyColumnStyles(sheet, data.getStyles().getColumnStyles(), data);
            }
            if(data.getStyles().getPositionedStyles() != null && priority.equals(data.getStyles().getPositionedStyles().getPriority())) {
                applyPositionedStyles(sheet, data.getStyles().getPositionedStyles(), data);
            }
            if(data.getStyles().getRectangleRangedStylesBuilder() != null && priority.equals(data.getStyles().getRectangleRangedStylesBuilder().getPriority())) {
                applyRangedStyles(sheet, data.getStyles().getRectangleRangedStylesBuilder(), data);
            }
        }
    }

    private void applyRowStyles(Sheet sheet, VerticalRangedStylesBuilder rowStyles) {
        final Collection<VerticalRangedStyle> styles = rowStyles.getStyles();
        for (VerticalRangedStyle style : styles) {
            final VerticalRange range = style.getRange();
            checkRange(range, sheet);
            for (int i = range.getStart(); i <= range.getEnd(); i++) {
                final Row row = sheet.getRow(i);
                if(row != null) {
                    for (int y = 0; y < row.getLastCellNum(); y++) {
                        cellApplyStyles(row.getCell(y), style);
                    }
                    if(style.getRowHeight() != null) {
                        row.setHeightInPoints(style.getRowHeight());
                    }
                }
            }
        }
    }

    private void applyColumnStyles(Sheet sheet, HorizontalRangedStylesBuilder columnStyles, ReportData reportData) {
        final Collection<HorizontalRangedStyle> styles = columnStyles.getStyles();
        for (HorizontalRangedStyle style : styles) {
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                final Row row = sheet.getRow(i);
                if(row != null) {
                    final HorizontalRange range = style.getRange();
                    checkRange(range, reportData);
                    for (int y = range.getStart(); y <= range.getEnd(); y++) {
                        cellApplyStyles(row.getCell(y), style);
                    }
                }
            }
            if(style.getColumnWidth() != null) {
                for (int i = style.getRange().getStart(); i <= style.getRange().getEnd(); i++) {
                    sheet.setColumnWidth(i, style.getColumnWidth() * 256);
                }
            }
        }
    }

    private void applyPositionedStyles(Sheet sheet, PositionedStylesBuilder positionedStyles, ReportData reportData) {
        final Collection<PositionedStyle> styles = positionedStyles.getStyles();
        for (PositionedStyle style : styles) {
            checkPosition(style.getRange(), sheet, reportData);
            final Row row = sheet.getRow(style.getRange().getRow());
            if(row != null && row.getCell(style.getRange().getColumn()) != null) {
                cellApplyStyles(row.getCell(style.getRange().getColumn()), style);
            }

        }
    }

    private void applyRangedStyles(Sheet sheet, RectangleRangedStylesBuilder rectangleRangedStyles, ReportData reportData) {
        final Collection<RectangleRangedStyle> rangedStyles = rectangleRangedStyles.getStyles();
        for (RectangleRangedStyle rangedStyle : rangedStyles) {
            final RectangleRange range = rangedStyle.getRange();
            final VerticalRange verticalRange = range.getVerticalRange();
            checkRange(verticalRange, sheet);
            final HorizontalRange horizontalRange = range.getHorizontalRange();
            checkRange(horizontalRange, reportData);
            for (int i = verticalRange.getStart(); i <= verticalRange.getEnd(); i++) {
                final Row row = sheet.getRow(i);
                if(row != null){
                    for (int y = horizontalRange.getStart(); y <= horizontalRange.getEnd(); y++) {
                        cellApplyStyles(row.getCell(y), rangedStyle);
                    }
                }
            }
        }
    }

    private void checkPosition(Position position, Sheet sheet, ReportData reportData) {
        if(Objects.isNull(position.getRow())) {
            position.setRow(sheet.getLastRowNum());
        } else if(position.getRow() < 0) {
            position.setRow(sheet.getLastRowNum() + position.getRow() + 1);
        }

        if(Objects.isNull(position.getColumn())) {
            position.setColumn(reportData.getColumnsCount() - 1);
        } else if(position.getColumn() < 0) {
            position.setColumn(reportData.getColumnsCount() + position.getColumn() - 1);
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

    private void cellApplyStyles(Cell cell, ReportStyle<?> style) {
        if(cell != null) {
            XSSFCellStyle cellStyle;
            final Pair<ReportStyle<?>, String> styleKey = Pair.of(style, cell.getCellStyle().getDataFormatString());
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

    private void cellApplyOtherStyles(ReportStyle<?> style, XSSFCellStyle cellStyle) {
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

        if(style instanceof HorizontalRangedStyle && Boolean.TRUE.equals(((HorizontalRangedStyle) style).getWrapText())) {
            cellStyle.setWrapText(true);
        }
    }

    private void cellApplyAlignmentStyles(ReportStyle<?> style, XSSFCellStyle cellStyle) {
        if(style.getHorizontalAlignment() != null) {
            cellStyle.setAlignment(style.getHorizontalAlignment());
        }
        if(style.getVerticalAlignment() != null) {
            cellStyle.setVerticalAlignment(style.getVerticalAlignment());
        }
    }

    private void cellApplyFontStyles(ReportStyle<?> style, XSSFCellStyle cellStyle) {
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

    private void cellApplyColorStyles(ReportStyle<?> style, XSSFCellStyle cellStyle) {
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

    private void cellApplyBorderStyles(ReportStyle<?> style, XSSFCellStyle cellStyle) {
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
