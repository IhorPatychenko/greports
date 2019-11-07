import java.util.Collection;
import java.util.stream.Collectors;

public class ReportHeaderCell extends ReportCell {
    private String title;

    public static Collection<ReportHeaderCell> from(Collection<ReportDataColumn> columns) {
        return columns.stream().map(column -> new ReportHeaderCell(column.getPosition(), column.getTitle())).collect(Collectors.toList());
    }

    public ReportHeaderCell(String position) {
        super(position);
    }

    public ReportHeaderCell(String position, String title) {
        super(position);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
