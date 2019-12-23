package content.cell;

public class ReportHeaderCell extends ReportCell {
    private String title;
    private boolean autoSizeColumn;

    public ReportHeaderCell(Float position, String title, boolean autoSizeColumn) {
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
