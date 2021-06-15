package org.greports.engine;

import org.greports.utils.AnnotationUtils;
import org.greports.utils.TranslationsParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Configuration implements Cloneable, Serializable {

    private static final long serialVersionUID = 5728699559958112658L;

    private String sheetName;
    private String[] reportName = new String[]{};
    private String translationsDir = "i18n/";
    private String locale = "en_US";
    private TranslationsParser.FileExtensions translationFileExtension;
    private boolean createHeader = true;
    private boolean stickyHeader = false;
    private boolean sortableHeader = false;
    private int headerRowIndex = 0;
    private int dataStartRowIndex = 1;
    private short verticalOffset = 0;
    private short horizontalOffset = 0;
    private List<SpecialRow> specialRows = new ArrayList<>();
    private List<SpecialColumn> specialColumns = new ArrayList<>();
    private boolean showGridlines = true;
    private boolean displayZeros = true;

    public static Configuration load(Class<?> clazz, String reportName) {
        return new Configuration(AnnotationUtils.getReportConfiguration(clazz, reportName));
    }

    Configuration(org.greports.annotations.Configuration configuration) {
        this.reportName = configuration.reportName();
        this.translationsDir = configuration.translationsDir();
        this.translationFileExtension = configuration.translationFileExtension();
        this.locale = configuration.locale();
        this.sheetName = configuration.sheetName();
        this.createHeader = configuration.createHeader();
        this.stickyHeader = configuration.stickyHeader();
        this.sortableHeader = configuration.sortableHeader();
        this.headerRowIndex = configuration.headerRowIndex();
        this.dataStartRowIndex = configuration.dataStartRowIndex();
        this.verticalOffset = configuration.verticalOffset();
        this.horizontalOffset = configuration.horizontalOffset();
        this.specialRows = Arrays.stream(configuration.specialRows()).map(SpecialRow::new).collect(Collectors.toList());
        this.specialColumns = Arrays.stream(configuration.specialColumns()).map(SpecialColumn::new).collect(Collectors.toList());
        this.showGridlines = configuration.showGridlines();
        this.displayZeros = configuration.displayZeros();
    }

    public Configuration(final String sheetName) {
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

    public TranslationsParser.FileExtensions getTranslationFileExtension() {
        return translationFileExtension;
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

    public short getVerticalOffset() {
        return verticalOffset;
    }

    public short getHorizontalOffset() {
        return horizontalOffset;
    }

    public List<SpecialRow> getSpecialRows() {
        return specialRows;
    }

    public List<SpecialColumn> getSpecialColumns() {
        return specialColumns;
    }

    public boolean isShowGridlines() {
        return showGridlines;
    }

    public boolean isDisplayZeros() {
        return displayZeros;
    }

    public Configuration setReportName(final String[] reportName) {
        this.reportName = reportName;
        return this;
    }

    public Configuration setTranslationsDir(final String translationsDir) {
        this.translationsDir = translationsDir;
        return this;
    }

    public Configuration setLocale(final String locale) {
        this.locale = locale;
        return this;
    }

    public Configuration setTranslationFileExtension(TranslationsParser.FileExtensions translationFileExtension) {
        this.translationFileExtension = translationFileExtension;
        return this;
    }

    public Configuration setSheetName(final String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public Configuration setCreateHeader(final boolean createHeader) {
        this.createHeader = createHeader;
        return this;
    }

    public Configuration setStickyHeader(boolean stickyHeader) {
        this.stickyHeader = stickyHeader;
        return this;
    }

    public Configuration setSortableHeader(final boolean sortableHeader) {
        this.sortableHeader = sortableHeader;
        return this;
    }

    public Configuration setHeaderRowIndex(final short headerRowIndex) {
        this.headerRowIndex = headerRowIndex;
        return this;
    }

    public Configuration setDataStartRowIndex(final short dataStartRowIndex) {
        this.dataStartRowIndex = dataStartRowIndex;
        return this;
    }

    public Configuration setVerticalOffset(short verticalOffset) {
        this.verticalOffset = verticalOffset;
        return this;
    }

    public Configuration setHorizontalOffset(short horizontalOffset) {
        this.horizontalOffset = horizontalOffset;
        return this;
    }

    public Configuration setSpecialRows(final List<SpecialRow> specialRows) {
        this.specialRows = specialRows;
        return this;
    }

    public Configuration setSpecialColumns(final List<SpecialColumn> specialColumns) {
        this.specialColumns = specialColumns;
        return this;
    }

    public Configuration setShowGridlines(boolean showGridlines) {
        this.showGridlines = showGridlines;
        return this;
    }

    public Configuration setDisplayZeros(boolean displayZeros) {
        this.displayZeros = displayZeros;
        return this;
    }

    @Override
    public Object clone() {
        Configuration clone = this;
        try {
            clone = (Configuration) super.clone();
            clone.specialRows = specialRows.stream().map(row -> (SpecialRow) row.clone()).collect(Collectors.toList());
            clone.specialColumns = specialColumns.stream().map(row -> (SpecialColumn) row.clone()).collect(Collectors.toList());
        } catch (CloneNotSupportedException ignored) {}
        return clone;
    }
}
