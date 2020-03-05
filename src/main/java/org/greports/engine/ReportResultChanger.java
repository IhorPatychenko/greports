package org.greports.engine;

import org.greports.content.ReportData;
import org.greports.exceptions.ReportEngineRuntimeException;

public class ReportResultChanger {

    private final ReportGeneratorResult reportGeneratorResult;

    public ReportResultChanger(final ReportGeneratorResult reportGeneratorResult) {
        this.reportGeneratorResult = reportGeneratorResult;
    }

    private ReportData getReportDataBySheetName(final String sheetName) {
        return reportGeneratorResult.getReportData().stream()
                .filter(rd -> rd.getSheetName().equals(sheetName))
                .findFirst()
                .orElse(null);
    }

    public ReportResultChanger cloneSheet(final String sourceSheetName, final String targetSheetName) {
        if(sourceSheetName == null || targetSheetName == null || sourceSheetName.equals(targetSheetName)) {
            throw new ReportEngineRuntimeException("Error cloning the sheet. The names of the origin and destination tabs cannot be null, nor can they have the same name", this.getClass());
        }
        final ReportData first = getReportDataBySheetName(sourceSheetName);
        if(first != null) {
            try {
                final ReportData clone = (ReportData) first.clone();
                clone.setSheetName(targetSheetName);
                reportGeneratorResult.getReportData().add(clone);
            } catch (CloneNotSupportedException e) {
                throw new ReportEngineRuntimeException("Clone not supported", this.getClass());
            }
        } else {
            throw new ReportEngineRuntimeException(String.format("Sheet with name %s wasn't found", sourceSheetName), this.getClass());
        }
        return this;
    }

    public ReportResultChanger changeCellValue(final String sheetName, final int rowIndex, final int columnIndex, final Object newValue) {
        final ReportData reportData = getReportDataBySheetName(sheetName);
        reportData.getPhysicalRow(rowIndex).getCell(columnIndex).setValue(newValue);
        return this;
    }
}
