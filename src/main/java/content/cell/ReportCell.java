package content.cell;

public abstract class ReportCell {
    private Float position;
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

    protected String getTitle() {
        return title;
    }
}
