package org.greports.engine;

import org.greports.annotations.SpecialColumn;

import java.io.Serializable;

public class ReportSpecialColumn implements Cloneable, Serializable {
    private static final long serialVersionUID = -4461036741398777369L;

    private float position;
    private String title = "";
    private String value;
    private String format = "";
    private String id = "";
    private ValueType valueType = ValueType.PLAIN_VALUE;
    private boolean autoSizeColumn = false;
    private int columnWidth = 1;

    ReportSpecialColumn(SpecialColumn specialColumn) {
        this.position = specialColumn.position();
        this.title = specialColumn.title();
        this.value = specialColumn.value();
        this.format = specialColumn.format();
        this.id = specialColumn.id();
        this.valueType = specialColumn.valueType();
        this.autoSizeColumn = specialColumn.autoSizeColumn();
        this.columnWidth = specialColumn.columnWidth();
    }

    public ReportSpecialColumn(final float position, final String value) {
        this.position = position;
        this.value = value;
    }

    public ReportSpecialColumn(final float position, final String value, final String id, final String title, final String format, final ValueType valueType, final boolean autoSizeColumn, final int columnWidth) {
        this(position, value);
        this.value = value;
        this.format = format;
        this.id = id;
        this.valueType = valueType;
        this.autoSizeColumn = autoSizeColumn;
        this.columnWidth = columnWidth;
    }

    public float getPosition() {
        return position;
    }

    public ReportSpecialColumn setPosition(final float position) {
        this.position = position;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ReportSpecialColumn setTitle(final String title) {
        this.title = title;
        return this;
    }

    public String getValue() {
        return value;
    }

    public ReportSpecialColumn setValue(final String value) {
        this.value = value;
        return this;
    }

    public String getFormat() {
        return format;
    }

    public ReportSpecialColumn setFormat(final String format) {
        this.format = format;
        return this;
    }

    public String getId() {
        return id;
    }

    public ReportSpecialColumn setId(final String id) {
        this.id = id;
        return this;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public ReportSpecialColumn setValueType(final ValueType valueType) {
        this.valueType = valueType;
        return this;
    }

    public boolean isAutoSizeColumn() {
        return autoSizeColumn;
    }

    public ReportSpecialColumn setAutoSizeColumn(final boolean autoSizeColumn) {
        this.autoSizeColumn = autoSizeColumn;
        return this;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    public ReportSpecialColumn setColumnWidth(final int columnWidth) {
        this.columnWidth = columnWidth;
        return this;
    }

    @Override
    public Object clone() {
        ReportSpecialColumn clone = this;
        try {
            clone = (ReportSpecialColumn) super.clone();
        } catch (CloneNotSupportedException ignored) {}
        return clone;
    }
}
