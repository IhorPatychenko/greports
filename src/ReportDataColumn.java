public class ReportDataColumn extends ReportCell {

    private Object value;

    public ReportDataColumn(String position) {
        super(position);
    }

    ReportDataColumn(String position, String value) {
        super(position);
        this.value = value;
    }

    ReportDataColumn(String position, String title, String value) {
        super(position, title);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    void setValue(Object value) {
        this.value = value;
    }
}
