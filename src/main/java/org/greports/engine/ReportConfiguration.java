package org.greports.engine;

import org.greports.annotations.Configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReportConfiguration implements Cloneable, Serializable {
    private static final long serialVersionUID = 5728699559958112658L;


    private String sheetName;
    private String[] reportName = new String[]{};
    private String translationsDir = "i18n/";
    private String locale = "en_US";
    private String templatePath = "";
    private boolean createHeader = true;
    private boolean stickyHeader = false;
    private boolean sortableHeader = false;
    private int headerRowIndex = 0;
    private int dataStartRowIndex = 1;
    private boolean templatedInject = false;
    private short verticalOffset = 0;
    private short horizontalOffset = 0;
    private List<ReportSpecialRow> specialRows = new ArrayList<>();
    private List<ReportSpecialColumn> specialColumns = new ArrayList<>();
    private boolean showGridlines = true;

    ReportConfiguration(Configuration configuration) {
        this.reportName = configuration.reportName();
        this.translationsDir = configuration.translationsDir();
        this.locale = configuration.locale();
        this.templatePath = configuration.templatePath();
        this.sheetName = configuration.sheetName();
        this.createHeader = configuration.createHeader();
        this.stickyHeader = configuration.stickyHeader();
        this.sortableHeader = configuration.sortableHeader();
        this.headerRowIndex = configuration.headerRowIndex();
        this.dataStartRowIndex = configuration.dataStartRowIndex();
        this.templatedInject = configuration.templatedInject();
        this.verticalOffset = configuration.verticalOffset();
        this.horizontalOffset = configuration.horizontalOffset();
        this.specialRows = Arrays.stream(configuration.specialRows()).map(ReportSpecialRow::new).collect(Collectors.toList());
        this.specialColumns = Arrays.stream(configuration.specialColumns()).map(ReportSpecialColumn::new).collect(Collectors.toList());
        this.showGridlines = configuration.showGridlines();
    }

    public ReportConfiguration(final String sheetName) {
        this.sheetName = sheetName;
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

    public boolean isStickyHeader() {
        return stickyHeader;
    }

    public boolean isSortableHeader() {
        return sortableHeader;
    }

    public int getHeaderRowIndex() {
        return headerRowIndex;
    }

    public int getDataStartRowIndex() {
        return dataStartRowIndex;
    }

    public boolean isTemplatedInject() {
        return templatedInject;
    }

    public short getVerticalOffset() {
        return verticalOffset;
    }

    public short getHorizontalOffset() {
        return horizontalOffset;
    }

    public List<ReportSpecialRow> getSpecialRows() {
        return specialRows;
    }

    public List<ReportSpecialColumn> getSpecialColumns() {
        return specialColumns;
    }

    public boolean isShowGridlines() {
        return showGridlines;
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

    public ReportConfiguration setStickyHeader(boolean stickyHeader) {
        this.stickyHeader = stickyHeader;
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

    public ReportConfiguration setTemplatedInject(final boolean templatedInject) {
        this.templatedInject = templatedInject;
        return this;
    }

    public ReportConfiguration setVerticalOffset(short verticalOffset) {
        this.verticalOffset = verticalOffset;
        return this;
    }

    public ReportConfiguration setHorizontalOffset(short horizontalOffset) {
        this.horizontalOffset = horizontalOffset;
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

    public ReportConfiguration setShowGridlines(boolean showGridlines) {
        this.showGridlines = showGridlines;
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
