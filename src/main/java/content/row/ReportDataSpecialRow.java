package content.row;

import content.cell.ReportDataSpecialRowCell;

import java.util.ArrayList;
import java.util.List;

public class ReportDataSpecialRow {
    private int index;
    private List<ReportDataSpecialRowCell> specialCells = new ArrayList<>();

    public ReportDataSpecialRow(int index){
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<ReportDataSpecialRowCell> getSpecialCells() {
        return specialCells;
    }

    public void addCell(ReportDataSpecialRowCell cell) {
        this.specialCells.add(cell);
    }
}
