import content.cell.ReportDataColumn;
import content.cell.ReportHeaderCell;
import content.ReportData;
import content.ReportHeader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import styles.HorizontalRangedStyle;
import styles.PositionedStyle;
import styles.RectangleRangedStyle;
import styles.ReportStyle;
import styles.ReportStylesBuilder;
import styles.ReportStylesBuilder.StylePriority;
import styles.VerticalRangedStyle;
import styles.interfaces.StripedRows;
import positioning.HorizontalRange;
import positioning.RectangleRange;
import positioning.VerticalRange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

class ReportDataInjector {

    private ReportData reportData;
    private Workbook wb = new XSSFWorkbook();
    private CreationHelper creationHelper = wb.getCreationHelper();

    ReportDataInjector(ReportData reportData){
        this.reportData = reportData;
    }

    void inject(){
        createSheets();
    }

    private void createSheets(){
        final Sheet sheet = wb.createSheet();
        createRows(sheet);
        addStripedRows(sheet);
        addStyles(sheet);
    }

    private void createRows(Sheet sheet){
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
            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(format));
            cell.setCellStyle(cellStyle);
        }
    }

    private void addStripedRows(Sheet sheet){
        final StripedRows.StripedRowsIndex stripedRowsIndex = reportData.getStyles().getStripedRowsIndex();
        final IndexedColors stripedRowsColor = reportData.getStyles().getStripedRowsColor();
        if(stripedRowsIndex != null && stripedRowsColor != null){
            for(int i = stripedRowsIndex.getIndex(); i <= sheet.getLastRowNum(); i+=2){
                final Row row = sheet.getRow(i);
                for(int y = 0; y < row.getLastCellNum(); y++) {
                    final Cell cell = row.getCell(y);
                    final CellStyle cellStyle = wb.createCellStyle();
                    cellStyle.cloneStyleFrom(cell.getCellStyle());
                    cellStyle.setFillForegroundColor(stripedRowsColor.getIndex());
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(cellStyle);
                }
            }
        }
    }

    private void addStyles(Sheet sheet) {
        for (StylePriority priority : StylePriority.values()) {
            if(reportData.getStyles().getRowStyles() != null && priority.equals(reportData.getStyles().getRowStyles().getPriority())){
                applyRowStyles(sheet, reportData.getStyles().getRowStyles());
            } else if(reportData.getStyles().getColumnStyles() != null && priority.equals(reportData.getStyles().getColumnStyles().getPriority())){
                applyColumnStyles(sheet, reportData.getStyles().getColumnStyles());
            } else if(reportData.getStyles().getPositionedStyles() != null && priority.equals(reportData.getStyles().getPositionedStyles().getPriority())){
                applyPositionedStyles(sheet, reportData.getStyles().getPositionedStyles());
            } else if(reportData.getStyles().getRangedStyleReportStyles() != null && priority.equals(reportData.getStyles().getRangedStyleReportStyles().getPriority())){
                applyRangedStyles(sheet, reportData.getStyles().getRangedStyleReportStyles());
            }
        }
    }

    private void applyRowStyles(Sheet sheet, ReportStylesBuilder<VerticalRangedStyle> rowStyles) {
        final Collection<VerticalRangedStyle> styles = rowStyles.build();
        for (VerticalRangedStyle style : styles) {
            final VerticalRange range = style.getRange();
            checkRangeEnd(range);
            for(int i = range.getStart(); i <= range.getEnd(); i++){
                final Row row = sheet.getRow(i);
                for(int y = 0; y < row.getLastCellNum(); y++){
                    cellApplyStyles(row.getCell(y), style);
                }
            }
        }
    }

    private void applyColumnStyles(Sheet sheet, ReportStylesBuilder<HorizontalRangedStyle> columnStyles) {
        final Collection<HorizontalRangedStyle> styles = columnStyles.build();
        for (HorizontalRangedStyle style : styles) {
            for(int i = 0; i <= sheet.getLastRowNum(); i++) {
                final Row row = sheet.getRow(i);
                final HorizontalRange range = style.getRange();
                checkRangeEnd(range);
                for(int y = range.getStart(); y <= range.getEnd(); y++){
                    cellApplyStyles(row.getCell(y), style);
                }
            }
        }
    }

    private void applyPositionedStyles(Sheet sheet, ReportStylesBuilder<PositionedStyle> positionedStyles) {
        final Collection<PositionedStyle> styles = positionedStyles.build();
        for (PositionedStyle style : styles) {
            cellApplyStyles(sheet.getRow(style.getPosition().getRow()).getCell(style.getPosition().getColumn()), style);
        }
    }

    private void applyRangedStyles(Sheet sheet, ReportStylesBuilder<RectangleRangedStyle> positionedStyles) {
        final Collection<RectangleRangedStyle> rangedStyles = positionedStyles.build();
        for (RectangleRangedStyle rangedStyle : rangedStyles) {
            final RectangleRange range = rangedStyle.getRange();
            final VerticalRange verticalRange = range.getVerticalRange();
            checkRangeEnd(verticalRange);
            final HorizontalRange horizontalRange = range.getHorizontalRange();
            checkRangeEnd(horizontalRange);
            for(int i = verticalRange.getStart(); i <= verticalRange.getEnd(); i++) {
                for(int y = horizontalRange.getStart(); y <= horizontalRange.getEnd(); y++){
                    cellApplyStyles(sheet.getRow(i).getCell(y), rangedStyle);
                }
            }
        }
    }

    private void checkRangeEnd(VerticalRange range){
        if(Objects.isNull(range.getEnd())) {
            range.setEnd(reportData.getRowsCount() + reportData.getDataStartRow() - 1);
        }
    }

    private void checkRangeEnd(HorizontalRange range){
        if(Objects.isNull(range.getEnd())){
            range.setEnd(reportData.getColumnsLength());
        }
    }

    private void cellApplyStyles(Cell cell, ReportStyle style) {
        final CellStyle cellStyle = wb.createCellStyle();

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
            cellStyle.setFillForegroundColor(style.getForegroundColor().getIndex());
            cellStyle.setFillPattern(style.getFillPattern());
        }

        // Font
        Font font = wb.createFont();
        if(style.getFontColor() != null) {
            font.setColor(style.getFontColor().getIndex());
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


    protected void writeToFileOutputStream(OutputStream fileOutputStream) throws IOException {
        wb.write(fileOutputStream);
        fileOutputStream.close();
        wb.close();
    }
}
