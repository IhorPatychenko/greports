package org.greports.content.cell;

import lombok.Getter;
import org.greports.engine.ValueType;

@Getter
public class SpecialDataCell extends AbstractReportCell implements ReportCell {
    private final String targetId;
    private final int columnWidth;
    private final String comment;
    private final short commentWidth;
    private final short commentHeight;
    private Object valuesById;

    public SpecialDataCell(final ValueType valueType, final Object value, final String format, final String targetId, final String comment, final short commentWidth, final short commentHeight, final int columnWidth) {
        super(value, format, valueType);
        this.targetId = targetId;
        this.comment = comment;
        this.commentWidth = commentWidth;
        this.commentHeight = commentHeight;
        this.columnWidth = columnWidth;
    }

    public SpecialDataCell setValuesById(final Object valuesById) {
        this.valuesById = valuesById;
        return this;
    }
}
