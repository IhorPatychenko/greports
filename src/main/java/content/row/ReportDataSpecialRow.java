package content.row;

import content.cell.ReportDataSpecialCell;

import java.util.ArrayList;
import java.util.List;

public class ReportDataSpecialRow {
    private int index;
    private List<ReportDataSpecialCell> specialCells = new ArrayList<>();

    public ReportDataSpecialRow(int index){
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<ReportDataSpecialCell> getSpecialCells() {
        return specialCells;
    }

    public void addCell(ReportDataSpecialCell cell) {
        this.specialCells.add(cell);
    }
}
