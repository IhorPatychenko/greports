package content.cell;

public abstract class ReportCell {
    private final Float position;
    private String title;

    public ReportCell(Float position) {
        this.position = position;
    }

    public ReportCell(Float position, String title) {
        this(position);
        this.title = title;
    }

    public Float getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    protected void setTitle(String newTitle) {
        this.title = newTitle;
    }
}
