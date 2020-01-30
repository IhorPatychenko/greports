package engine;

import content.ReportData;
import content.ReportHeader;
import content.column.ReportDataCell;
import content.cell.ReportDataSpecialRowCell;
import content.cell.ReportHeaderCell;
import content.row.ReportDataSpecialRow;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import positioning.HorizontalRange;
import positioning.Position;
import positioning.RectangleRange;
import positioning.VerticalRange;
import styles.HorizontalRangedStyle;
import styles.PositionedStyle;
import styles.RectangleRangedStyle;
import styles.ReportStyle;
import styles.stylesbuilders.AbstractReportStylesBuilder;
import styles.VerticalRangedStyle;
import styles.interfaces.StripedRows;
import styles.stylesbuilders.HorizontalRangedStylesBuilder;
import styles.stylesbuilders.PositionedStylesBuilder;
import styles.stylesbuilders.RectangleRangedStylesBuilder;
import styles.stylesbuilders.VerticalRangedStylesBuilder;
import utils.Pair;
import utils.Utils;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class ReportDataRawInjector extends ReportDataInjector {

    private final ReportData reportData;
    private Map<Pair<ReportStyle<?>, String>, XSSFCellStyle> _stylesCache = new HashedMap<>();

    public ReportDataRawInjector(XSSFWorkbook currentWorkbook, ReportData reportData) {
        super(currentWorkbook, reportData);
        this.reportData = reportData;
    }

    @Override
    public void inject() {
        Sheet sheet = getSheet(currentWorkbook, reportData);
        _stylesCache = new HashedMap<>();
        _formatsCache = new HashMap<>();
        injectData(sheet);
    }

    private Sheet getSheet(Workbook workbook, ReportData reportData){
        if(reportData.getSheetName() == null){
            return workbook.createSheet();
        } else {
            return workbook.createSheet(reportData.getSheetName());
        }
    }

    protected void injectData(Sheet sheet) {
        createHeader(sheet);
        createDataRows(sheet);
        createSpecialRows(sheet);
        addStripedRows(sheet);
        addStyles(sheet);
        adjustColumns(sheet);
    }

    private void createHeader(Sheet sheet) {
        if(reportData.isCreateHeader()){
            final ReportHeader header = reportData.getHeader();
            final Row headerRow = sheet.createRow(reportData.getHeaderRowIndex());
            for (int i = 0; i < header.getCells().size(); i++) {
                createHeaderCell(headerRow, header.getCells().get(i), i);
            }
            if(header.isColumnFilter()){
                sheet.setAutoFilter(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), 0, header.getCells().size() - 1));
            }
        }
    }

    private void createDataRows(Sheet sheet){
        // First create cells with data
        for (int i = 0; i < reportData.getRows().size(); i++) {
            Row dataRow = sheet.createRow(reportData.getDataStartRow() + i);
            for (int y = 0; y < reportData.getRow(i).getCells().size(); y++) {
                final ReportDataCell column = reportData.getRow(i).getColumn(y);
                if(!column.getValueType().equals(ValueType.FORMULA)){
                    createCell(dataRow, column, y);
                }
            }
        }
        // After create cells with formulas to can evaluate
        for (int i = 0; i < reportData.getRows().size(); i++) {
            Row dataRow = sheet.getRow(reportData.getDataStartRow() + i);
            for (int y = 0; y < reportData.getRow(i).getCells().size(); y++) {
                final ReportDataCell column = reportData.getRow(i).getColumn(y);
                if(column.getValueType().equals(ValueType.FORMULA)){
                    createCell(dataRow, column, y);
                }
            }
        }
    }

    private void createHeaderCell(Row row, ReportHeaderCell headerCell, int cellIndex){
        final Cell cell = row.createCell(cellIndex);
        WorkbookUtils.setCellValue(cell, headerCell.getTitle());
    }

    private void createCell(Row row, ReportDataCell reportDataCell, int cellIndex){
        CellType cellType = CellType.BLANK;
        final ValueType valueType = reportDataCell.getValueType();
        if(!ValueType.FORMULA.equals(valueType)){
            if(reportDataCell.getValue() instanceof Number){
                cellType = CellType.NUMERIC;
            } else if(reportDataCell.getValue() instanceof String){
                cellType = CellType.STRING;
            } else if(reportDataCell.getValue() instanceof Boolean) {
                cellType = CellType.BOOLEAN;
            }
            final Cell cell = row.createCell(cellIndex, cellType);
            WorkbookUtils.setCellValue(cell, reportDataCell.getValue());
            setCellFormat(cell, reportDataCell.getFormat());
        } else {
            cellType = CellType.FORMULA;
            final Cell cell = row.createCell(cellIndex, cellType);
            String formulaString = reportDataCell.getValue().toString();
            for (Map.Entry<String, Integer> entry : reportData.getTargetIndexes().entrySet()) {
                formulaString = formulaString.replaceAll(entry.getKey(), super.getCellReferenceForTargetId(row, entry.getKey()).formatAsString());
            }
            cell.setCellFormula(formulaString);
            setCellFormat(cell, reportDataCell.getFormat());
        }
    }

    private void createSpecialRows(Sheet sheet) {
        final List<ReportDataSpecialRow> specialRows = reportData.getSpecialRows();
        for (int i = 0; i < specialRows.size(); i++) {
            ReportDataSpecialRow specialRow = specialRows.get(i);
            if(specialRow.getIndex() == Integer.MAX_VALUE) {
                specialRow.setIndex(reportData.getDataStartRow() + reportData.getRowsCount() + i);
            }
            for (final ReportDataSpecialRowCell specialCell : specialRow.getSpecialCells()) {
                if(sheet.getRow(specialRow.getIndex()) == null){
                    sheet.createRow(specialRow.getIndex());
                }
                final Cell cell = sheet.getRow(specialRow.getIndex()).createCell(reportData.getColumnIndexForTarget(specialCell.getTargetId()));
                final ValueType valueType = specialCell.getValueType();
                if(!ValueType.FORMULA.equals(valueType)){
                    WorkbookUtils.setCellValue(cell, specialCell.getValue());
                } else {
                    String formulaString = specialCell.getValue().toString();
                    for (Map.Entry<String, Integer> entry : reportData.getTargetIndexes().entrySet()) {
                        CellReference firstCellReference = super.getCellReferenceForTargetId(sheet.getRow(reportData.getDataStartRow()), specialCell.getTargetId());
                        CellReference lastCellReference = super.getCellReferenceForTargetId(sheet.getRow(reportData.getDataStartRow() + reportData.getRowsCount() - 1), specialCell.getTargetId());
                        formulaString = formulaString.replaceAll(entry.getKey(), firstCellReference.formatAsString() + ":" + lastCellReference.formatAsString());
                    }
                    cell.setCellFormula(formulaString);
                }
                setCellFormat(cell, specialCell.getFormat());
            }
        }
        XSSFFormulaEvaluator.evaluateAllFormulaCells(currentWorkbook);
    }

    private void addStripedRows(Sheet sheet){
        final StripedRows.StripedRowsIndex stripedRowsIndex = reportData.getStyles().getStripedRowsIndex();
        final Color stripedRowsColor = reportData.getStyles().getStripedRowsColor();
        if(stripedRowsIndex != null && stripedRowsColor != null){
            for(int i = stripedRowsIndex.getIndex(); i <= sheet.getLastRowNum(); i+=2){
                final Row row = sheet.getRow(i);
                for(int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
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
            if(reportData.getStyles().getRowStyles() != null && priority.equals(reportData.getStyles().getRowStyles().getPriority())){
                applyRowStyles(sheet, reportData.getStyles().getRowStyles());
            }
            if(reportData.getStyles().getColumnStyles() != null && priority.equals(reportData.getStyles().getColumnStyles().getPriority())){
                applyColumnStyles(sheet, reportData.getStyles().getColumnStyles(), reportData);
            }
            if(reportData.getStyles().getPositionedStyles() != null && priority.equals(reportData.getStyles().getPositionedStyles().getPriority())){
                applyPositionedStyles(sheet, reportData.getStyles().getPositionedStyles(), reportData);
            }
            if(reportData.getStyles().getRectangleRangedStylesBuilder() != null && priority.equals(reportData.getStyles().getRectangleRangedStylesBuilder().getPriority())){
                applyRangedStyles(sheet, reportData.getStyles().getRectangleRangedStylesBuilder(), reportData);
            }
        }
    }

    private void applyRowStyles(Sheet sheet, VerticalRangedStylesBuilder rowStyles) {
        final Collection<VerticalRangedStyle> styles = rowStyles.getStyles();
        for (VerticalRangedStyle style : styles) {
            final VerticalRange range = style.getRange();
            checkRange(range, sheet);
            for(int i = range.getStart(); i <= range.getEnd(); i++){
                final Row row = sheet.getRow(i);
                for(int y = 0; y < row.getLastCellNum(); y++){
                    cellApplyStyles(row.getCell(y), style);
                }
                if(style.getRowHeight() != null) {
                    row.setHeightInPoints(style.getRowHeight());
                }
            }
        }
    }

    private void applyColumnStyles(Sheet sheet, HorizontalRangedStylesBuilder columnStyles, ReportData reportData) {
        final Collection<HorizontalRangedStyle> styles = columnStyles.getStyles();
        for (HorizontalRangedStyle style : styles) {
            for(int i = 0; i <= sheet.getLastRowNum(); i++) {
                final Row row = sheet.getRow(i);
                final HorizontalRange range = style.getRange();
                checkRange(range, reportData);
                for(int y = range.getStart(); y <= range.getEnd(); y++){
                    cellApplyStyles(row.getCell(y), style);
                }
            }
            if(style.getColumnWidth() != null){
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
            cellApplyStyles(sheet.getRow(style.getRange().getRow()).getCell(style.getRange().getColumn()), style);
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
            for(int i = verticalRange.getStart(); i <= verticalRange.getEnd(); i++) {
                for(int y = horizontalRange.getStart(); y <= horizontalRange.getEnd(); y++){
                    cellApplyStyles(sheet.getRow(i).getCell(y), rangedStyle);
                }
            }
        }
    }

    private void checkPosition(Position position, Sheet sheet, ReportData reportData) {
        if(Objects.isNull(position.getRow())){
            position.setRow(sheet.getLastRowNum());
        } else if(position.getRow() < 0) {
            position.setRow(sheet.getLastRowNum() + position.getRow() + 1);
        }

        if(Objects.isNull(position.getColumn())){
            position.setColumn(reportData.getColumnsCount() - 1);
        } else if(position.getColumn() < 0){
            position.setColumn(reportData.getColumnsCount() + position.getColumn() - 1);
        }
    }

    private void checkRange(VerticalRange range, Sheet sheet){
        if(Objects.isNull(range.getStart())){
            range.setStart(sheet.getLastRowNum());
        } else if(range.getStart() < 0){
            range.setStart(sheet.getLastRowNum() + range.getStart() + 1);
        }

        if(Objects.isNull(range.getEnd())) {
            range.setEnd(sheet.getLastRowNum());
        } else if(range.getEnd() < 0) {
            range.setEnd(sheet.getLastRowNum() + range.getEnd());
        }
    }

    private void checkRange(HorizontalRange range, ReportData reportData){
        if(Objects.isNull(range.getStart())){
            range.setStart(reportData.getColumnsCount() - 1);
        } else if(range.getStart() < 0){
            range.setStart(reportData.getColumnsCount() + range.getStart() - 1);
        }

        if(Objects.isNull(range.getEnd())) {
            range.setEnd(reportData.getColumnsCount() - 1);
        } else if(range.getEnd() < 0) {
            range.setEnd(reportData.getColumnsCount() + range.getEnd() - 1);
        }
    }

    private void cellApplyStyles(Cell cell, ReportStyle<?> style) {
        if(cell != null){
            XSSFCellStyle cellStyle;
            final Pair<ReportStyle<?>, String> styleKey = Pair.of(style, cell.getCellStyle().getDataFormatString());
            if(!_stylesCache.containsKey(styleKey) || style.isClonePreviousStyle()){
                cellStyle = currentWorkbook.createCellStyle();
                cellStyle.setDataFormat(cell.getCellStyle().getDataFormat());
                if(style.isClonePreviousStyle()){
                    cellStyle.cloneStyleFrom(cell.getCellStyle());
                }
                // Borders
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

                // Colors
                if(style.getForegroundColor() != null) {
                    cellStyle.setFillForegroundColor(new XSSFColor(style.getForegroundColor()));
                    cellStyle.setFillPattern(style.getFillPattern());
                }
                if(style.getBorderColor() != null){
                    cellStyle.setBorderColor(XSSFCellBorder.BorderSide.TOP, new XSSFColor(style.getBorderColor()));
                    cellStyle.setBorderColor(XSSFCellBorder.BorderSide.RIGHT, new XSSFColor(style.getBorderColor()));
                    cellStyle.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM, new XSSFColor(style.getBorderColor()));
                    cellStyle.setBorderColor(XSSFCellBorder.BorderSide.LEFT, new XSSFColor(style.getBorderColor()));
                }

                // Font
                if(Utils.anyNotNull(style.getFontColor(), style.getBoldFont(), style.getItalicFont(), style.getUnderlineFont(), style.getStrikeoutFont())){
                    XSSFFont font = currentWorkbook.createFont();
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
                    if(style.getStrikeoutFont() != null){
                        font.setStrikeout(style.getStrikeoutFont());
                    }
                    cellStyle.setFont(font);
                }

                // Alignment
                if(style.getHorizontalAlignment() != null) {
                    cellStyle.setAlignment(style.getHorizontalAlignment());
                }
                if(style.getVerticalAlignment() != null) {
                    cellStyle.setVerticalAlignment(style.getVerticalAlignment());
                }

                _stylesCache.put(styleKey, cellStyle);
            } else {
                cellStyle = _stylesCache.get(styleKey);
            }
            cell.setCellStyle(cellStyle);
        }
    }

    private void adjustColumns(Sheet sheet) {
        for (Integer autoSizedColumn : reportData.getAutoSizedColumns()) {
            sheet.autoSizeColumn(autoSizedColumn);
        }
    }
}
