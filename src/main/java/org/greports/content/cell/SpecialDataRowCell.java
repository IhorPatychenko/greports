package org.greports.content.cell;

import lombok.Getter;
import org.greports.annotations.SpecialRowCell;
import org.greports.engine.ValueType;

@Getter
public class SpecialDataRowCell extends AbstractReportCell implements ReportCell {
    private final String targetId;
    private final int columnWidth;
    private final String comment;
    private final short commentWidth;
    private final short commentHeight;
    private Object valuesById;

    public SpecialDataRowCell(final ValueType valueType, final Object value, final String format, final String targetId, final String comment, final short commentWidth, final short commentHeight, final int columnWidth) {
        super(value, format, valueType);
        this.targetId = targetId;
        this.comment = comment;
        this.commentWidth = commentWidth;
        this.commentHeight = commentHeight;
        this.columnWidth = columnWidth;
    }

    public SpecialDataRowCell(SpecialRowCell cell) {
        this(cell.valueType(), cell.value(), cell.format(), cell.targetId(), cell.comment(), cell.commentWidth(), cell.commentHeight(), cell.columnWidth());
    }

    public SpecialDataRowCell setValuesById(final Object valuesById) {
        this.valuesById = valuesById;
        return this;
    }
}
