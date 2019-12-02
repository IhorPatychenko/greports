package cell;

public class ReportDataColumn extends ReportCell {

    private Object value;

    public ReportDataColumn(String position) {
        super(position);
    }

    public ReportDataColumn(String position, String value) {
        super(position);
        this.value = value;
    }

    public ReportDataColumn(String position, String title, String value) {
        super(position, title);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
