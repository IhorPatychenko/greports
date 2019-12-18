package content.cell;

public class ReportDataColumn extends ReportCell {

    private Object value;
    private String format;
    private String id;

    public ReportDataColumn(Float position, String format, Object value, String id) {
        this(position, null, format, value, id);
    }

    public ReportDataColumn(Float position, String title, String format, Object value, String id) {
        super(position, title);
        this.format = format;
        this.value = value;
        this.id = id;
    }

    public Object getValue() {
        return value;
    }

    public String getFormat() {
        return format;
    }

    public String getId() {
        return id;
    }
}
