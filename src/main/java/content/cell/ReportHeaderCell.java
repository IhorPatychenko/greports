package content.cell;

import java.util.Collection;
import java.util.stream.Collectors;

public class ReportHeaderCell extends ReportCell {
    private String title;
    private boolean autoSizeColumn;

    public static Collection<ReportHeaderCell> fromEmptyColumns(Collection<ReportDataColumn> columns) {
        return columns.stream().map(column -> new ReportHeaderCell(column.getPosition(), column.getTitle(), false)).collect(Collectors.toList());
    }

    public ReportHeaderCell(String position, String title, boolean autoSizeColumn) {
        super(position);
        this.title = title;
        this.autoSizeColumn = autoSizeColumn;
    }

    public String getTitle() {
        return title;
    }

    public boolean isAutoSizeColumn() {
        return autoSizeColumn;
    }
}
