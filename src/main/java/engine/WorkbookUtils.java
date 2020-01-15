package engine;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;

import java.util.Date;
import java.util.Objects;

class WorkbookUtils {

    protected static void cloneWorkbook(XSSFWorkbook sourceWorkbook, XSSFWorkbook currentWorkbook){
        final StylesTable oldStylesSource = sourceWorkbook.getStylesSource();
        final StylesTable newStylesSource = currentWorkbook.getStylesSource();

        oldStylesSource.getFonts().forEach(font -> newStylesSource.putFont(font, true));
        oldStylesSource.getFills().forEach(newStylesSource::putFill);
        oldStylesSource.getBorders().forEach(border -> newStylesSource.putBorder(new XSSFCellBorder(border.getCTBorder())));

        for (int sheetNumber = 0; sheetNumber < sourceWorkbook.getNumberOfSheets(); sheetNumber++) {
            final XSSFSheet oldSheet = sourceWorkbook.getSheetAt(sheetNumber);
            final XSSFSheet newSheet = currentWorkbook.createSheet(oldSheet.getSheetName());

            newSheet.setDefaultRowHeight(oldSheet.getDefaultRowHeight());
            newSheet.setDefaultColumnWidth(oldSheet.getDefaultColumnWidth());

            for (int rowNumber = oldSheet.getFirstRowNum(); rowNumber <= oldSheet.getLastRowNum(); rowNumber++) {
                final XSSFRow oldRow = oldSheet.getRow(rowNumber);
                if (oldRow != null) {
                    final XSSFRow newRow = newSheet.createRow(rowNumber);
                    newRow.setHeight(oldRow.getHeight());
                    for (int columnNumber = oldRow.getFirstCellNum(); columnNumber < oldRow.getLastCellNum(); columnNumber++) {
                        newSheet.setColumnWidth(columnNumber, oldSheet.getColumnWidth(columnNumber));
                        final XSSFCell oldCell = oldRow.getCell(columnNumber);
                        if (oldCell != null) {
                            final XSSFCell newCell = newRow.createCell(columnNumber);
                            if(CellType.FORMULA.equals(oldCell.getCellTypeEnum())){
                                newCell.setCellFormula(oldCell.getCellFormula());
                            } else {
                                setCellValue(newCell, getCellValue(oldCell));
                            }
                            XSSFCellStyle newCellStyle = currentWorkbook.createCellStyle();
                            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
                            newCell.setCellStyle(newCellStyle);
                        }
                    }
                }
            }
        }
    }

    protected static void setCellValue(Cell cell, Object value) {
        if(value instanceof Date) {
            cell.setCellValue(((Date) value));
        } else if(value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if(value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(Objects.toString(value, ""));
        }
    }

    private static Object getCellValue(final XSSFCell cell) {
        switch (cell.getCellTypeEnum()) {
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case ERROR:
                return cell.getErrorCellValue();
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
            case BLANK:
                return cell.getStringCellValue();
            case FORMULA:
                return  "=" + cell.getCellFormula();
            default:
                throw new IllegalArgumentException();
        }
    }

}
