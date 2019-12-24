package content.column;

import content.cell.ReportCell;
import engine.ValueType;

import java.util.List;

public class ReportDataCell extends ReportCell {

    private Object value;
    private String format;
    private List<String> targetIds;
    private ValueType valueType;

    public ReportDataCell(Float position, String format, Object value, List<String> targetIds, ValueType valueType) {
        super(position, null);
        this.format = format;
        this.value = value;
        this.targetIds = targetIds;
        this.valueType = valueType;
    }

    public Object getValue() {
        return value;
    }

    public String getFormat() {
        return format;
    }

    public List<String> getTargetIds() {
        return targetIds;
    }

    public ValueType getValueType() {
        return valueType;
    }
}
