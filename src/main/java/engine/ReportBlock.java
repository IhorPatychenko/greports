package engine;

import java.util.ArrayList;
import java.util.List;

public class ReportBlock {

    private Integer startColumn;
    private Integer endColumn;
    private List<ReportColumn> reportColumns = new ArrayList<>();
    private boolean isRepeatable;

    public ReportBlock(final Integer startColumn) {
        this.startColumn = startColumn;
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
}
