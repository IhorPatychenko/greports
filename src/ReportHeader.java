import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ReportHeader {
    private Collection<ReportHeaderCell> cells = new ArrayList<>();

    public Collection<ReportHeaderCell> getCells() {
        return cells;
    }

    void addCell(@NotNull ReportHeaderCell cell) {
        this.cells.add(cell);
    }

    ReportHeader addCells(@NotNull Collection<ReportHeaderCell> cells) {
        this.cells.addAll(cells);
        return this;
    }

    ReportHeader sortCells(){
        this.cells = cells.stream().sorted(Comparator.comparing(ReportHeaderCell::getPosition)).collect(Collectors.toList());
        return this;
    }
}
