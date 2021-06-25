package org.greports.engine;

import org.greports.annotations.SpecialRow;
import org.greports.content.row.SpecialDataRow;
import org.greports.styles.interfaces.StyledReport;
import org.greports.utils.AnnotationUtils;
import org.greports.utils.TranslationsParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Configuration implements Cloneable, Serializable {

    private static final long serialVersionUID = 5728699559958112658L;

    private String[] reportName = new String[]{};
    private String translationsDir = "i18n/";
    private TranslationsParser.FileExtensions translationFileExtension;
    private String locale = "en_US";
    private String sheetName;
    private Class<? extends StyledReport> styles;
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
        this.styles = configuration.styles();
        this.createHeader = configuration.createHeader();
        this.stickyHeader = configuration.stickyHeader();
        this.sortableHeader = configuration.sortableHeader();
        this.headerRowIndex = configuration.headerRowIndex();
        this.dataStartRowIndex = configuration.dataStartRowIndex();
        this.verticalOffset = configuration.verticalOffset();
        this.horizontalOffset = configuration.horizontalOffset();
        this.specialRows = Arrays.asList(configuration.specialRows());
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

    public Configuration setReportName(String[] reportName) {
        this.reportName = reportName;
        return this;
    }

    public String getTranslationsDir() {
        return translationsDir;
    }

    public Configuration setTranslationsDir(String translationsDir) {
        this.translationsDir = translationsDir;
        return this;
    }

    public TranslationsParser.FileExtensions getTranslationFileExtension() {
        return translationFileExtension;
    }

    public Configuration setTranslationFileExtension(TranslationsParser.FileExtensions translationFileExtension) {
        this.translationFileExtension = translationFileExtension;
        return this;
    }

    public String getLocale() {
        return locale;
    }

    public Configuration setLocale(String locale) {
        this.locale = locale;
        return this;
    }

    public String getSheetName() {
        return sheetName;
    }

    public Configuration setSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public Class<? extends StyledReport> getStyles() {
        return styles;
    }

    public Configuration setStyles(Class<? extends StyledReport> styles) {
        this.styles = styles;
        return this;
    }

    public boolean isCreateHeader() {
        return createHeader;
    }

    public Configuration setCreateHeader(boolean createHeader) {
        this.createHeader = createHeader;
        return this;
    }

    public boolean isStickyHeader() {
        return stickyHeader;
    }

    public Configuration setStickyHeader(boolean stickyHeader) {
        this.stickyHeader = stickyHeader;
        return this;
    }

    public boolean isSortableHeader() {
        return sortableHeader;
    }

    public Configuration setSortableHeader(boolean sortableHeader) {
        this.sortableHeader = sortableHeader;
        return this;
    }

    public int getHeaderRowIndex() {
        return headerRowIndex;
    }

    public Configuration setHeaderRowIndex(int headerRowIndex) {
        this.headerRowIndex = headerRowIndex;
        return this;
    }

    public int getDataStartRowIndex() {
        return dataStartRowIndex;
    }

    public Configuration setDataStartRowIndex(int dataStartRowIndex) {
        this.dataStartRowIndex = dataStartRowIndex;
        return this;
    }

    public short getVerticalOffset() {
        return verticalOffset;
    }

    public Configuration setVerticalOffset(short verticalOffset) {
        this.verticalOffset = verticalOffset;
        return this;
    }

    public short getHorizontalOffset() {
        return horizontalOffset;
    }

    public Configuration setHorizontalOffset(short horizontalOffset) {
        this.horizontalOffset = horizontalOffset;
        return this;
    }

    public List<SpecialRow> getSpecialRows() {
        return specialRows;
    }

    public Configuration setSpecialRows(List<SpecialRow> specialRows) {
        this.specialRows = specialRows;
        return this;
    }

    public List<SpecialColumn> getSpecialColumns() {
        return specialColumns;
    }

    public Configuration setSpecialColumns(List<SpecialColumn> specialColumns) {
        this.specialColumns = specialColumns;
        return this;
    }

    public boolean isShowGridlines() {
        return showGridlines;
    }

    public Configuration setShowGridlines(boolean showGridlines) {
        this.showGridlines = showGridlines;
        return this;
    }

    public boolean isDisplayZeros() {
        return displayZeros;
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
            clone.specialRows = specialRows;
            clone.specialColumns = specialColumns.stream().map(row -> (SpecialColumn) row.clone()).collect(Collectors.toList());
        } catch (CloneNotSupportedException ignored) {}
        return clone;
    }
}
