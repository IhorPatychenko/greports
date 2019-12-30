package engine;

import content.ReportData;
import content.ReportHeader;
import content.column.ReportDataCell;
import content.cell.ReportDataSpecialRowCell;
import content.cell.ReportHeaderCell;
import content.row.ReportDataSpecialRow;
import formula.FormulaBuilder;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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
import styles.ReportStylesBuilder;
import styles.VerticalRangedStyle;
import styles.interfaces.StripedRows;
import utils.Pair;
import utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class ReportDataInjector {

    private final Collection<ReportData> reportData;
    private final XSSFWorkbook currentWorkbook = new XSSFWorkbook();
    private CreationHelper creationHelper = currentWorkbook.getCreationHelper();
    private final Map<Pair, XSSFCellStyle> _stylesCache = new HashedMap<>();
    private final Map<String, XSSFCellStyle> _formatsCache = new HashMap<>();

    ReportDataInjector(Collection<ReportData> reportData){
        this.reportData = reportData;
    }

    void inject(){
        createSheets();
    }

    private void createSheets(){
        for (final ReportData data : reportData) {
            Sheet sheet;
            if(data.getSheetName() == null){
                sheet = currentWorkbook.createSheet();
            } else {
                sheet = currentWorkbook.createSheet(data.getSheetName());
            }
            injectData(sheet, data);
        }
    }

    private void injectData(Sheet sheet, ReportData reportData) {
        createHeader(sheet, reportData);
        createDataRows(sheet, reportData);
        createSpecialRows(sheet, reportData);
        addStripedRows(sheet, reportData);
        addStyles(sheet, reportData);
        adjustColumns(sheet, reportData);
    }

    private void createHeader(Sheet sheet, ReportData reportData) {
        if(reportData.isShowHeader()){
            final ReportHeader header = reportData.getHeader();
            final Row headerRow = sheet.createRow(reportData.getHeaderStartRow());
            for (int i = 0; i < header.getCells().size(); i++) {
                createHeaderCell(headerRow, header.getCells().get(i), i);
            }
            if(header.isColumnFilter()){
                sheet.setAutoFilter(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), 0, header.getCells().size() - 1));
            }
        }
    }

    private void createDataRows(Sheet sheet, ReportData reportData){
        // First create cells with data
        for (int i = 0; i < reportData.getRows().size(); i++) {
            Row dataRow = sheet.createRow(reportData.getDataStartRow() + i);
            for (int y = 0; y < reportData.getRow(i).getCells().size(); y++) {
                final ReportDataCell column = reportData.getRow(i).getColumn(y);
                createDataCell(dataRow, column, y);
            }
        }
        // After create cells with formulas to can evaluate
        for (int i = 0; i < reportData.getRows().size(); i++) {
            Row dataRow = sheet.getRow(reportData.getDataStartRow() + i);
            for (int y = 0; y < reportData.getRow(i).getCells().size(); y++) {
                final ReportDataCell column = reportData.getRow(i).getColumn(y);
                createFormulaCell(dataRow, column, y, reportData);
            }
        }
    }

    private void createHeaderCell(Row row, ReportHeaderCell headerCell, int cellIndex){
        final Cell cell = row.createCell(cellIndex);
        setCellValue(cell, headerCell.getTitle());
    }

    private void createDataCell(Row row, ReportDataCell reportDataCell, int cellIndex){
        final ValueType valueType = reportDataCell.getValueType();
        if(!ValueType.FORMULA.equals(valueType)){
            final Cell cell = row.createCell(cellIndex);
            setCellValue(cell, reportDataCell.getValue());
            setCellFormat(cell, reportDataCell.getFormat());
        }
    }

    private void createFormulaCell(Row row, ReportDataCell reportDataCell, int cellIndex, ReportData reportData) {
        if(reportDataCell.getValueType().equals(ValueType.FORMULA)){
            final Cell cell = row.createCell(cellIndex);
            String formulaString = new FormulaBuilder(reportDataCell.getValue().toString(), reportDataCell.isRangedFormula(), reportDataCell.getTargetIds().size()).build();
            for (String targetId : reportDataCell.getTargetIds()) {
                final int columnIndexForTarget = reportData.getColumnIndexForTarget(targetId);
                CellReference cellReference = new CellReference(row.getCell(columnIndexForTarget));
                formulaString = formulaString.replaceFirst(FormulaBuilder.FORMULA_TOKENIZER, cellReference.formatAsString());
            }
            cell.setCellFormula(formulaString);
            setCellFormat(cell, reportDataCell.getFormat());
        }
    }

    private void setCellValue(Cell cell, Object value) {
        if(value instanceof Date){
            cell.setCellValue(((Date) value));
        } else if(value instanceof Number){
            cell.setCellValue(((Number) value).doubleValue());
        } else if(value instanceof Boolean){
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(Objects.toString(value, ""));
        }
    }

    private void setCellFormat(Cell cell, String format) {
        if(format != null && !format.isEmpty()){
            XSSFCellStyle cellStyle;
            if(!_formatsCache.containsKey(format)){
                cellStyle = currentWorkbook.createCellStyle();
                cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(format));
                _formatsCache.put(format, cellStyle);
            } else {
                cellStyle = _formatsCache.get(format);
            }
            cell.setCellStyle(cellStyle);
        }
    }

    private void createSpecialRows(Sheet sheet, ReportData reportData) {
        final List<ReportDataSpecialRow> specialRows = reportData.getSpecialRows();
        for (final ReportDataSpecialRow specialRow : specialRows) {
            if(specialRow.getIndex() == Integer.MAX_VALUE) {
                specialRow.setIndex(reportData.getDataStartRow() + reportData.getRowsCount());
            }
            final Row row = sheet.createRow(specialRow.getIndex());
            for(int i = 0; i < reportData.getColumnsCount(); i++){
                final Cell cell = row.createCell(i);
                setCellValue(cell, "");
            }
            for (final ReportDataSpecialRowCell specialCell : specialRow.getSpecialCells()) {
                final Cell cell = row.createCell(reportData.getColumnIndexForTarget(specialCell.getTargetId()));
                final ValueType valueType = specialCell.getValueType();
                if(!ValueType.FORMULA.equals(valueType)){
                    setCellValue(cell, specialCell.getValue());
                    setCellFormat(cell, specialCell.getFormat());
                } else {
                    final String formula = new FormulaBuilder(specialCell.getValue().toString(), true, 1).build();
                    final int columnIndexForTarget = reportData.getColumnIndexForTarget(specialCell.getTargetId());
                    CellReference firstCellReference = new CellReference(sheet.getRow(reportData.getDataStartRow()).getCell(columnIndexForTarget));
                    CellReference lastCellReference = new CellReference(sheet.getRow(reportData.getDataStartRow() + reportData.getRowsCount() - 1).getCell(columnIndexForTarget));
                    final String replace = formula
                            .replace(FormulaBuilder.FORMULA_TOKENIZER, firstCellReference.formatAsString() + ":" + lastCellReference.formatAsString());
                    cell.setCellFormula(replace);
                    setCellFormat(cell, specialCell.getFormat());
                }
            }
        }
        XSSFFormulaEvaluator.evaluateAllFormulaCells(currentWorkbook);
    }

    private void addStripedRows(Sheet sheet, ReportData reportData){
        final StripedRows.StripedRowsIndex stripedRowsIndex = reportData.getStyles().getStripedRowsIndex();
        final IndexedColors stripedRowsColor = reportData.getStyles().getStripedRowsColor();
        if(stripedRowsIndex != null && stripedRowsColor != null){
            for(int i = stripedRowsIndex.getIndex(); i <= sheet.getLastRowNum(); i+=2){
                final Row row = sheet.getRow(i);
                for(int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
                    final Cell cell = row.getCell(y);
                    final CellStyle cellStyle = currentWorkbook.createCellStyle();
                    cellStyle.cloneStyleFrom(cell.getCellStyle());
                    cellStyle.setFillForegroundColor(stripedRowsColor.getIndex());
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(cellStyle);
                }
            }
        }
    }

    private void addStyles(Sheet sheet, ReportData reportData) {
        for (ReportStylesBuilder.StylePriority priority : ReportStylesBuilder.StylePriority.values()) {
            if(reportData.getStyles().getRowStyles() != null && priority.equals(reportData.getStyles().getRowStyles().getPriority())){
                applyRowStyles(sheet, reportData.getStyles().getRowStyles());
            }
            if(reportData.getStyles().getColumnStyles() != null && priority.equals(reportData.getStyles().getColumnStyles().getPriority())){
                applyColumnStyles(sheet, reportData.getStyles().getColumnStyles(), reportData);
            }
            if(reportData.getStyles().getPositionedStyles() != null && priority.equals(reportData.getStyles().getPositionedStyles().getPriority())){
                applyPositionedStyles(sheet, reportData.getStyles().getPositionedStyles(), reportData);
            }
            if(reportData.getStyles().getRangedStyleReportStyles() != null && priority.equals(reportData.getStyles().getRangedStyleReportStyles().getPriority())){
                applyRangedStyles(sheet, reportData.getStyles().getRangedStyleReportStyles(), reportData);
            }
        }
    }

    private void applyRowStyles(Sheet sheet, ReportStylesBuilder<VerticalRangedStyle> rowStyles) {
        final Collection<VerticalRangedStyle> styles = rowStyles.getStyles();
        for (VerticalRangedStyle style : styles) {
            final VerticalRange range = style.getRange();
            checkRange(range, sheet);
            for(int i = range.getStart(); i <= range.getEnd(); i++){
                final Row row = sheet.getRow(i);
                for(int y = 0; y < row.getLastCellNum(); y++){
                    cellApplyStyles(row.getCell(y), style);
                }
            }
        }
    }

    private void applyColumnStyles(Sheet sheet, ReportStylesBuilder<HorizontalRangedStyle> columnStyles, ReportData reportData) {
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
        }
    }

    private void applyPositionedStyles(Sheet sheet, ReportStylesBuilder<PositionedStyle> positionedStyles, ReportData reportData) {
        final Collection<PositionedStyle> styles = positionedStyles.getStyles();
        for (PositionedStyle style : styles) {
            checkPosition(style.getPosition(), sheet, reportData);
            cellApplyStyles(sheet.getRow(style.getPosition().getRow()).getCell(style.getPosition().getColumn()), style);
        }
    }

    private void applyRangedStyles(Sheet sheet, ReportStylesBuilder<RectangleRangedStyle> positionedStyles, ReportData reportData) {
        final Collection<RectangleRangedStyle> rangedStyles = positionedStyles.getStyles();
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

    private void cellApplyStyles(Cell cell, ReportStyle style) {
        XSSFCellStyle cellStyle;
        final Pair<ReportStyle, String> styleKey = new Pair<>(style, cell.getCellStyle().getDataFormatString());
        if(!_stylesCache.containsKey(styleKey)){
            cellStyle = currentWorkbook.createCellStyle();
            cellStyle.cloneStyleFrom(cell.getCellStyle());

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

    private void adjustColumns(Sheet sheet, ReportData data) {
        for (Integer autoSizedColumn : data.getAutoSizedColumns()) {
            sheet.autoSizeColumn(autoSizedColumn);
        }
    }

    synchronized void writeToFileOutputStream(OutputStream fileOutputStream) throws IOException {
        currentWorkbook.write(fileOutputStream);
        fileOutputStream.close();
        currentWorkbook.close();
    }


}
