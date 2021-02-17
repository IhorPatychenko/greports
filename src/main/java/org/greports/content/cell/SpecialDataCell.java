package org.greports.content.cell;

import org.greports.engine.ValueType;

public class SpecialDataCell extends AbstractReportCell implements ReportCell {
    private final ValueType valueType;
    private final String targetId;
    private final int columnWidth;
    private final String comment;
    private final short commentWidth;
    private final short commentHeight;
    private Object valuesById;

    public SpecialDataCell(final ValueType valueType, final Object value, final String format, final String targetId, final String comment, final short commentWidth, final short commentHeight, final int columnWidth) {
        super(value, format);
        this.valueType = valueType;
        this.targetId = targetId;
        this.comment = comment;
        this.commentWidth = commentWidth;
        this.commentHeight = commentHeight;
        this.columnWidth = columnWidth;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public String getTargetId() {
        return targetId;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    public Object getValuesById() {
        return valuesById;
    }

    public String getComment() {
        return comment;
    }

    public short getCommentWidth() {
        return commentWidth;
    }

    public short getCommentHeight() {
        return commentHeight;
    }

    public SpecialDataCell setValuesById(final Object valuesById) {
        this.valuesById = valuesById;
        return this;
    }
}
