package content.cell;

public class ReportHeaderCell extends ReportCell {
    private String title;
    private String id;
    private boolean autoSizeColumn;

    public ReportHeaderCell(Float position, String title, boolean autoSizeColumn) {
        this(position, title, null, autoSizeColumn);
    }

    public ReportHeaderCell(Float position, String title, String id, boolean autoSizeColumn) {
        super(position);
        this.title = title;
        this.id = id;
        this.autoSizeColumn = autoSizeColumn;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public boolean isAutoSizeColumn() {
        return autoSizeColumn;
    }
}
