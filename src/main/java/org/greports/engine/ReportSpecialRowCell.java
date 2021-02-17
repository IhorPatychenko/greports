package org.greports.engine;

import org.greports.annotations.SpecialRowCell;

import java.io.Serializable;

public class ReportSpecialRowCell implements Cloneable, Serializable {
    private static final long serialVersionUID = -4555194088608860976L;

    private String targetId;
    private ValueType valueType;
    private String value;
    private String format;
    private String comment;
    private int columnWidth = 1;

    ReportSpecialRowCell(SpecialRowCell specialRowCell) {
        this(specialRowCell.targetId(),
            specialRowCell.valueType(),
            specialRowCell.value(),
            specialRowCell.format(),
            specialRowCell.comment(),
            specialRowCell.columnWidth()
        );
    }

    public ReportSpecialRowCell(final String targetId, final ValueType valueType, final String value, final String format, final String comment, final int columnWidth) {
        this.targetId = targetId;
        this.valueType = valueType;
        this.value = value;
        this.format = format;
        this.comment = comment;
        this.columnWidth = columnWidth;
    }

    public String getTargetId() {
        return targetId;
    }

    public ReportSpecialRowCell setTargetId(final String targetId) {
        this.targetId = targetId;
        return this;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public ReportSpecialRowCell setValueType(final ValueType valueType) {
        this.valueType = valueType;
        return this;
    }

    public String getValue() {
        return value;
    }

    public ReportSpecialRowCell setValue(final String value) {
        this.value = value;
        return this;
    }

    public String getFormat() {
        return format;
    }

    public ReportSpecialRowCell setFormat(final String format) {
        this.format = format;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public ReportSpecialRowCell setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    public ReportSpecialRowCell setColumnWidth(final int columnWidth) {
        this.columnWidth = columnWidth;
        return this;
    }

    @Override
    public Object clone() {
        ReportSpecialRowCell clone = this;
        try {
            clone = (ReportSpecialRowCell) super.clone();
        } catch (CloneNotSupportedException ignored) {}
        return clone;
    }
}
