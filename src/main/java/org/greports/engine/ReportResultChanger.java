package org.greports.engine;

import org.greports.exceptions.ReportEngineRuntimeException;

public class ReportResultChanger {

    private final ReportData reportData;
    private final ReportGeneratorResult reportGeneratorResult;

    public ReportResultChanger(final ReportData reportData, final ReportGeneratorResult reportGeneratorResult) {
        this.reportData = reportData;
        this.reportGeneratorResult = reportGeneratorResult;
    }

    public ReportResultChanger cloneSheet(final String targetSheetName) {
        if(targetSheetName == null || reportData.getConfiguration().getSheetName().equals(targetSheetName)) {
            throw new ReportEngineRuntimeException("Error cloning the sheet. The names of the origin and destination tabs cannot be null, nor can they have the same name", this.getClass());
        }
        try {
            final ReportData clone = (ReportData) reportData.clone();
            clone.setSheetName(targetSheetName);
            reportGeneratorResult.getReportData().add(clone);
        } catch (CloneNotSupportedException e) {
            throw new ReportEngineRuntimeException("Clone not supported", this.getClass());
        }
        return reportGeneratorResult.getResultChanger(targetSheetName);
    }

    public ReportResultChanger changeCellValue(final int rowIndex, final int columnIndex, final Object newValue) {
        if(reportData.isCellExist(rowIndex, columnIndex)){
            reportData.getPhysicalRow(rowIndex).getCell(columnIndex).setValue(newValue);
        }
        return this;
    }

    public ReportResultChanger changeCellFormat(final int rowIndex, final int columnIndex, final String format) {
        if(reportData.isCellExist(rowIndex, columnIndex)) {
            reportData.getPhysicalRow(rowIndex).getCell(columnIndex).setFormat(format);
        }
        return this;
    }

    public ReportResultChanger changeSheetName(final String newSheetName) {
        if(newSheetName == null) {
            throw new ReportEngineRuntimeException("Error changing sheet's name. New name cannot be null", this.getClass());
        }
        reportData.setSheetName(newSheetName);
        return this;
    }
}
