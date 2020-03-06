package org.greports.engine;

import org.greports.annotations.Configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReportConfiguration implements Cloneable, Serializable {
    private static final long serialVersionUID = -5662450749358381078L;

    private String sheetName;
    private String[] reportName = new String[]{};
    private String translationsDir = "i18n/";
    private String locale = "en_US";
    private String templatePath = "";
    private boolean createHeader = true;
    private boolean sortableHeader = false;
    private short headerRowIndex = 0;
    private short dataStartRowIndex = 1;
    private boolean useExistingSheet = false;
    private List<ReportSpecialRow> specialRows = new ArrayList<>();
    private List<ReportSpecialColumn> specialColumns = new ArrayList<>();

    ReportConfiguration(Configuration configuration) {
        this.reportName = configuration.reportName();
        this.translationsDir = configuration.translationsDir();
        this.locale = configuration.locale();
        this.templatePath = configuration.templatePath();
        this.sheetName = configuration.sheetName();
        this.createHeader = configuration.createHeader();
        this.sortableHeader = configuration.sortableHeader();
        this.headerRowIndex = configuration.headerRowIndex();
        this.dataStartRowIndex = configuration.dataStartRowIndex();
        this.useExistingSheet = configuration.useExistingSheet();
        this.specialRows = Arrays.stream(configuration.specialRows()).map(ReportSpecialRow::new).collect(Collectors.toList());
        this.specialColumns = Arrays.stream(configuration.specialColumns()).map(ReportSpecialColumn::new).collect(Collectors.toList());;
    }

    public ReportConfiguration(final String sheetName) {
        this.sheetName = sheetName;
    }

    public ReportConfiguration(final String sheetName, final String translationsDir, final String locale, final String templatePath, final boolean createHeader, final boolean sortableHeader, final short headerRowIndex, final short dataStartRowIndex, final boolean useExistingSheet, final List<ReportSpecialRow> specialRows, final List<ReportSpecialColumn> specialColumns) {
        this.sheetName = sheetName;
        this.translationsDir = translationsDir;
        this.locale = locale;
        this.templatePath = templatePath;
        this.createHeader = createHeader;
        this.sortableHeader = sortableHeader;
        this.headerRowIndex = headerRowIndex;
        this.dataStartRowIndex = dataStartRowIndex;
        this.useExistingSheet = useExistingSheet;
        this.specialRows = specialRows;
        this.specialColumns = specialColumns;
    }

    public String[] getReportName() {
        return reportName;
    }

    public String getTranslationsDir() {
        return translationsDir;
    }

    public String getLocale() {
        return locale;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public String getSheetName() {
        return sheetName;
    }

    public boolean isCreateHeader() {
        return createHeader;
    }

    public boolean isSortableHeader() {
        return sortableHeader;
    }

    public short getHeaderRowIndex() {
        return headerRowIndex;
    }

    public short getDataStartRowIndex() {
        return dataStartRowIndex;
    }

    public boolean isUseExistingSheet() {
        return useExistingSheet;
    }

    public List<ReportSpecialRow> getSpecialRows() {
        return specialRows;
    }

    public List<ReportSpecialColumn> getSpecialColumns() {
        return specialColumns;
    }

    public ReportConfiguration setReportName(final String[] reportName) {
        this.reportName = reportName;
        return this;
    }

    public ReportConfiguration setTranslationsDir(final String translationsDir) {
        this.translationsDir = translationsDir;
        return this;
    }

    public ReportConfiguration setLocale(final String locale) {
        this.locale = locale;
        return this;
    }

    public ReportConfiguration setTemplatePath(final String templatePath) {
        this.templatePath = templatePath;
        return this;
    }

    public ReportConfiguration setSheetName(final String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public ReportConfiguration setCreateHeader(final boolean createHeader) {
        this.createHeader = createHeader;
        return this;
    }

    public ReportConfiguration setSortableHeader(final boolean sortableHeader) {
        this.sortableHeader = sortableHeader;
        return this;
    }

    public ReportConfiguration setHeaderRowIndex(final short headerRowIndex) {
        this.headerRowIndex = headerRowIndex;
        return this;
    }

    public ReportConfiguration setDataStartRowIndex(final short dataStartRowIndex) {
        this.dataStartRowIndex = dataStartRowIndex;
        return this;
    }

    public ReportConfiguration setUseExistingSheet(final boolean useExistingSheet) {
        this.useExistingSheet = useExistingSheet;
        return this;
    }

    public ReportConfiguration setSpecialRows(final List<ReportSpecialRow> specialRows) {
        this.specialRows = specialRows;
        return this;
    }

    public ReportConfiguration setSpecialColumns(final List<ReportSpecialColumn> specialColumns) {
        this.specialColumns = specialColumns;
        return this;
    }

    @Override
    public Object clone() {
        ReportConfiguration clone = this;
        try {
            clone = (ReportConfiguration) super.clone();
            clone.specialRows = specialRows.stream().map(row -> (ReportSpecialRow) row.clone()).collect(Collectors.toList());
            clone.specialColumns = specialColumns.stream().map(row -> (ReportSpecialColumn) row.clone()).collect(Collectors.toList());
        } catch (CloneNotSupportedException ignored) {}
        return clone;
    }
}
