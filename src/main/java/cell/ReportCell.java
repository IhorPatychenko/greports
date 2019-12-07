package cell;

public abstract class ReportCell {
    private String position;
    private String title;

    public ReportCell(String position) {
        this.position = position;
    }

    public ReportCell(String position, String title) {
        this(position);
        this.title = title;
    }

    public String getPosition() {
        return position;
    }

    protected String getTitle() {
        return title;
    }
}
