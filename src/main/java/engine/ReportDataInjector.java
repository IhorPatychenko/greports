package engine;

import annotations.ReportSpecialCell;
import content.ReportData;
import content.ReportHeader;
import content.cell.ReportDataColumn;
import content.cell.ReportDataSpecialCell;
import content.cell.ReportHeaderCell;
import content.row.ReportDataSpecialRow;
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
import positioning.RectangleRange;
import positioning.VerticalRange;
import styles.HorizontalRangedStyle;
import styles.PositionedStyle;
import styles.RectangleRangedStyle;
import styles.ReportStyle;
import styles.ReportStylesBuilder;
import styles.VerticalRangedStyle;
import styles.interfaces.StripedRows;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static formula.Formulas.FIRST_CELL;
import static formula.Formulas.LAST_CELL;

class ReportDataInjector {

    private final Collection<ReportData> reportData;
    private final XSSFWorkbook currentWorkbook = new XSSFWorkbook();
    private CreationHelper creationHelper = currentWorkbook.getCreationHelper();

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

    private void injectData(Sheet sheet, ReportData data) {
        createRows(sheet, data);
        createSpecialRows(sheet, data);
        addStripedRows(sheet, data);
        addStyles(sheet, data);
        adjustColumns(sheet, data);
    }

    private void createRows(Sheet sheet, ReportData reportData){
        // Create a header row
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
        // Create data rows
        for (int i = 0; i < reportData.getRows().size(); i++) {
            Row dataRow = sheet.createRow(reportData.getDataStartRow() + i);
            for (int y = 0; y < reportData.getRow(i).getColumns().size(); y++) {
                final ReportDataColumn column = reportData.getRow(i).getColumn(y);
                createCell(dataRow, column.getValue(), column.getFormat(), y);
            }
        }
    }

    private void createHeaderCell(Row row, ReportHeaderCell headerCell, int cellIndex){
        createCell(row, headerCell.getTitle(), null, cellIndex);
    }

    private void createCell(Row row, Object value, String format, int cellIndex) {
        final Cell cell = row.createCell(cellIndex);
        setCellValue(cell, value);
        setCellFormat(cell, format);
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
            CellStyle cellStyle = currentWorkbook.createCellStyle();
            cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(format));
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
            for (final ReportDataSpecialCell specialCell : specialRow.getSpecialCells()) {
                final Cell cell = row.createCell(specialCell.getColumnIndex());
                CellReference firstCellReference = new CellReference(sheet.getRow(reportData.getDataStartRow() - 1).getCell(specialCell.getColumnIndex()));
                CellReference lastCellReference = new CellReference(sheet.getRow(reportData.getDataStartRow() + reportData.getRowsCount() - 1).getCell(specialCell.getColumnIndex()));
                if(ReportSpecialCell.ValueType.LITERAL.equals(specialCell.getValueType())){
                    setCellValue(cell, specialCell.getValue());
                } else if(ReportSpecialCell.ValueType.FORMULA.equals(specialCell.getValueType())){
                    final String replace = specialCell.getValue()
                            .replace(FIRST_CELL, firstCellReference.formatAsString())
                            .replace(LAST_CELL, lastCellReference.formatAsString());
                    cell.setCellFormula(replace);
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
                applyRowStyles(sheet, reportData.getStyles().getRowStyles(), reportData);
            } else if(reportData.getStyles().getColumnStyles() != null && priority.equals(reportData.getStyles().getColumnStyles().getPriority())){
                applyColumnStyles(sheet, reportData.getStyles().getColumnStyles(), reportData);
            } else if(reportData.getStyles().getPositionedStyles() != null && priority.equals(reportData.getStyles().getPositionedStyles().getPriority())){
                applyPositionedStyles(sheet, reportData.getStyles().getPositionedStyles());
            } else if(reportData.getStyles().getRangedStyleReportStyles() != null && priority.equals(reportData.getStyles().getRangedStyleReportStyles().getPriority())){
                applyRangedStyles(sheet, reportData.getStyles().getRangedStyleReportStyles(), reportData);
            }
        }
    }

    private void applyRowStyles(Sheet sheet, ReportStylesBuilder<VerticalRangedStyle> rowStyles, ReportData reportData) {
        final Collection<VerticalRangedStyle> styles = rowStyles.getStyles();
        for (VerticalRangedStyle style : styles) {
            final VerticalRange range = style.getRange();
            checkRange(range, reportData);
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

    private void applyPositionedStyles(Sheet sheet, ReportStylesBuilder<PositionedStyle> positionedStyles) {
        final Collection<PositionedStyle> styles = positionedStyles.getStyles();
        for (PositionedStyle style : styles) {
            cellApplyStyles(sheet.getRow(style.getPosition().getRow()).getCell(style.getPosition().getColumn()), style);
        }
    }

    private void applyRangedStyles(Sheet sheet, ReportStylesBuilder<RectangleRangedStyle> positionedStyles, ReportData reportData) {
        final Collection<RectangleRangedStyle> rangedStyles = positionedStyles.getStyles();
        for (RectangleRangedStyle rangedStyle : rangedStyles) {
            final RectangleRange range = rangedStyle.getRange();
            final VerticalRange verticalRange = range.getVerticalRange();
            checkRange(verticalRange, reportData);
            final HorizontalRange horizontalRange = range.getHorizontalRange();
            checkRange(horizontalRange, reportData);
            for(int i = verticalRange.getStart(); i <= verticalRange.getEnd(); i++) {
                for(int y = horizontalRange.getStart(); y <= horizontalRange.getEnd(); y++){
                    cellApplyStyles(sheet.getRow(i).getCell(y), rangedStyle);
                }
            }
        }
    }

    private void checkRange(VerticalRange range, ReportData reportData){
        if(Objects.isNull(range.getEnd())) {
            int end = reportData.getRowsCount() + reportData.getDataStartRow() + reportData.getSpecialRows().size() - 1;
            if (range.getStart() < 0){
                range.setStart(end + range.getStart() + 1);
            }
            range.setEnd(end);
        }
    }

    private void checkRange(HorizontalRange range, ReportData reportData){
        if(Objects.isNull(range.getEnd())){
            int end = reportData.getColumnsCount() - 1;
            if(range.getStart() < 0){
                range.setStart(end + range.getStart() + 1);
            }
            range.setEnd(end);
        }
    }

    private void cellApplyStyles(Cell cell, ReportStyle style) {
        final XSSFCellStyle cellStyle = currentWorkbook.createCellStyle();

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
        XSSFFont font = currentWorkbook.createFont();
        if(style.getFontColor() != null) {
            font.setColor(new XSSFColor(style.getFontColor()));
        }
        if(style.getBoldFont() != null) {
            font.setBold(style.getBoldFont());
        }

        cellStyle.setFont(font);

        // Alignment
        if(style.getHorizontalAlignment() != null) {
            cellStyle.setAlignment(style.getHorizontalAlignment());
        }
        if(style.getVerticalAlignment() != null) {
            cellStyle.setVerticalAlignment(style.getVerticalAlignment());
        }

        cell.setCellStyle(cellStyle);
    }

    private void adjustColumns(Sheet sheet, ReportData data) {
        for (Integer autoSizedColumn : data.getAutoSizedColumns()) {
            sheet.autoSizeColumn(autoSizedColumn);
        }
    }


    void writeToFileOutputStream(OutputStream fileOutputStream) throws IOException {
        currentWorkbook.write(fileOutputStream);
        fileOutputStream.close();
        currentWorkbook.close();
    }
}
