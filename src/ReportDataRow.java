import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ReportDataRow {
    private Collection<ReportDataColumn> columns = new ArrayList<>();

    ReportDataRow() {}

    public Collection<ReportDataColumn> getColumns() {
        return columns;
    }

    void addColumn(ReportDataColumn column){
        this.columns.add(column);
    }

    void orderColumns(){
        this.columns = columns.stream().sorted(Comparator.comparing(ReportDataColumn::getPosition)).collect(Collectors.toList());
    }
}
