package cell;

public class ReportDataColumn extends ReportCell {

    private Object value;
    private String format;
    private Class clazz;

    public ReportDataColumn(String position, String format, Object value) {
        this(position, null, format, value);
    }

    public ReportDataColumn(String position, String title, String format, Object value) {
        super(position, title);
        this.format = format;
        this.value = value;
        this.clazz = value != null ? value.getClass() : null;
    }

    public Object getValue() {
        return value;
    }

    public String getFormat() {
        return format;
    }

    public Class getDataClass() {
        return clazz;
    }
}
