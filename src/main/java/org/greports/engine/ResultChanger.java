package org.greports.engine;

import org.greports.exceptions.ReportEngineRuntimeException;

public class ResultChanger {

    private final Data data;
    private final GeneratorResult generatorResult;

    public ResultChanger(final Data data, final GeneratorResult generatorResult) {
        this.data = data;
        this.generatorResult = generatorResult;
    }

    public ResultChanger cloneSheet(final String targetSheetName) {
        if(targetSheetName == null || data.getConfiguration().getSheetName().equals(targetSheetName)) {
            throw new ReportEngineRuntimeException("Error cloning the sheet. The names of the origin and destination tabs cannot be null, nor can they have the same name", this.getClass());
        }
        try {
            final Data clone = (Data) data.clone();
            clone.setSheetName(targetSheetName);
            generatorResult.getReportData().add(clone);
        } catch (CloneNotSupportedException e) {
            throw new ReportEngineRuntimeException("Clone not supported", this.getClass());
        }
        return generatorResult.getResultChanger(targetSheetName);
    }

    public ResultChanger changeCellValue(final int rowIndex, final int columnIndex, final Object newValue) {
        return this.changeCellValue(rowIndex, columnIndex, newValue, ValueType.PLAIN_VALUE);
    }

    public ResultChanger changeCellValue(final int rowIndex, final int columnIndex, final Object newValue, final ValueType valueType) {
        if(data.isCellExist(rowIndex, columnIndex)){
            data.getPhysicalRow(rowIndex).getCell(columnIndex)
                    .setValue(newValue)
                    .setValueType(valueType);
        }
        return this;
    }

    public ResultChanger changeCellFormat(final int rowIndex, final int columnIndex, final String format) {
        if(data.isCellExist(rowIndex, columnIndex)) {
            data.getPhysicalRow(rowIndex).getCell(columnIndex).setFormat(format);
        }
        return this;
    }

    public ResultChanger changeSheetName(final String newSheetName) {
        if(newSheetName == null) {
            throw new ReportEngineRuntimeException("Error changing sheet's name. New name cannot be null", this.getClass());
        }
        generatorResult.updateResultChangerSheetName(data.getConfiguration().getSheetName(), newSheetName);
        data.setSheetName(newSheetName);
        return this;
    }
}
