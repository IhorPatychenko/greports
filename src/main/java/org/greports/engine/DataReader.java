package org.greports.engine;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greports.exceptions.GreportsRuntimeException;
import org.greports.utils.WorkbookUtils;

public class DataReader {

    private final XSSFWorkbook workbook;

    public DataReader(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public int getLastRowNum(int sheetIndex) {
        if(sheetIndex < 0 || sheetIndex >= workbook.getNumberOfSheets()) {
            throw new GreportsRuntimeException("sheetIndex cannot be lower than zeo and greater than workbook number of sheets", this.getClass());
        }
        return WorkbookUtils.getLastRowNum(this.workbook.getSheetAt(sheetIndex));
    }

    public Object getCellValue(int sheetIndex, Integer rowNumber, int cellNumber) {
        return this.getCellValue(sheetIndex, rowNumber, cellNumber, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    }

    public Object getCellValue(int sheetIndex, Integer rowNumber, int cellNumber, Row.MissingCellPolicy missingCellPolicy) {
        return this.getCellValue(sheetIndex, rowNumber, cellNumber, missingCellPolicy, Object.class);
    }

    public <T> T getCellValue(int sheetIndex, Integer rowNumber, int cellNumber, Row.MissingCellPolicy missingCellPolicy, Class<T> bindToClass) {
        if(sheetIndex < 0 || sheetIndex >= workbook.getNumberOfSheets()) {
            throw new GreportsRuntimeException("sheetIndex cannot be lower than zeo and greater than workbook number of sheets", this.getClass());
        }

        return bindToClass.cast(this.getCellValue(this.workbook.getSheetAt(sheetIndex), rowNumber, cellNumber, missingCellPolicy));
    }

    public Object getCellValue(String sheetName, Integer rowNumber, int cellNumber) {
        return this.getCellValue(sheetName, rowNumber, cellNumber, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    }

    public Object getCellValue(String sheetName, Integer rowNumber, int cellNumber, Row.MissingCellPolicy missingCellPolicy) {
        return this.getCellValue(sheetName, rowNumber, cellNumber, missingCellPolicy, Object.class);
    }

    public <T> T getCellValue(String sheetName, Integer rowNumber, int cellNumber, Class<T> bindToClass) {
        return this.getCellValue(sheetName, rowNumber, cellNumber, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK, bindToClass);
    }

    public <T> T getCellValue(String sheetName, Integer rowNumber, int cellNumber, Row.MissingCellPolicy missingCellPolicy, Class<T> bindToClass) {
        if(StringUtils.isEmpty(sheetName)) {
            throw new GreportsRuntimeException("sheetName cannot be null.", this.getClass());
        }

        final XSSFSheet sheet = this.workbook.getSheet(sheetName);

        if(sheet == null) {
            throw new GreportsRuntimeException(String.format("The sheet with name %s does not exist", sheetName), this.getClass());
        }

        return bindToClass.cast(this.getCellValue(sheet, rowNumber, cellNumber, missingCellPolicy));
    }

    public Object getCellValue(XSSFSheet sheet, Integer rowNumber, int cellNumber, Row.MissingCellPolicy missingCellPolicy) {
        return this.getCellValue(sheet, rowNumber, cellNumber, missingCellPolicy, Object.class);
    }

    public <T> T getCellValue(XSSFSheet sheet, Integer rowNumber, int cellNumber, Row.MissingCellPolicy missingCellPolicy, Class<T> bindToClass) {
        if(rowNumber == null) {
            rowNumber = WorkbookUtils.getLastRowNum(sheet);
        }

        if(rowNumber < 0) {
            rowNumber = WorkbookUtils.getLastRowNum(sheet) + rowNumber;
        }

        if(cellNumber < 0) {
            throw new GreportsRuntimeException("Cell index cannot be lower than zero.", this.getClass());
        }

        final XSSFRow row = sheet.getRow(rowNumber);
        if(row == null) {
            throw new GreportsRuntimeException(String.format("The row with index %d does not exist", rowNumber), this.getClass());
        }

        final XSSFCell cell = row.getCell(cellNumber, missingCellPolicy);

        return bindToClass.cast(this.getValue(cell));
    }

    private Object getValue(XSSFCell cell) {
        if(cell == null) {
            return null;
        }
        if(CellType.ERROR.equals(cell.getCellTypeEnum())) {
            return cell.getErrorCellString();
        } else if(CellType.BOOLEAN.equals(cell.getCellTypeEnum())) {
            return cell.getBooleanCellValue();
        } else if(CellType.NUMERIC.equals(cell.getCellTypeEnum()) && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        } else if(CellType.NUMERIC.equals(cell.getCellTypeEnum())) {
            return cell.getNumericCellValue();
        } else if(CellType.FORMULA.equals(cell.getCellTypeEnum())) {
            return cell.getCellFormula();
        } else {
            // CellType.STRING and CellType.BLANK
            return cell.getStringCellValue();
        }
    }
}
