package engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReportBlock {

    private Integer startColumn;
    private Integer endColumn;
    private List<ReportColumn> reportColumns = new ArrayList<>();
    private boolean isRepeatable;
    private Class<?> blockClass;
    private Field parentField;

    public ReportBlock(final Integer startColumn) {
        this.startColumn = startColumn;
    }

    public ReportBlock(final Integer startColumn, final Field parentField) {
        this(startColumn);
        this.parentField = parentField;
    }

    public Integer getStartColumn() {
        return startColumn;
    }

    public Integer getEndColumn() {
        return endColumn;
    }

    public Class<?> getBlockClass() {
        return blockClass;
    }

    public ReportBlock setBlockClass(final Class<?> blockClass) {
        this.blockClass = blockClass;
        return this;
    }

    public ReportBlock setEndColumn(final Integer endColumn) {
        this.endColumn = endColumn;
        return this;
    }

    public ReportBlock addReportColumn(final ReportColumn reportColumn) {
        reportColumns.add(reportColumn);
        return this;
    }

    public boolean isRepeatable() {
        return isRepeatable;
    }

    public ReportBlock setRepeatable(final boolean repeatable) {
        isRepeatable = repeatable;
        return this;
    }

    public ReportColumn getColumn(final int columnIndex) {
        return reportColumns.get(columnIndex);
    }

    public Field getParentField() {
        return parentField;
    }
}
