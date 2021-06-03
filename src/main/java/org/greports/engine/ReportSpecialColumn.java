package org.greports.engine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.greports.annotations.SpecialColumn;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class ReportSpecialColumn implements Cloneable, Serializable {
    private static final long serialVersionUID = -4461036741398777369L;

    private float position;
    private String title = "";
    private String value;
    private String format = "";
    private String id = "";
    private ValueType valueType = ValueType.PLAIN_VALUE;
    private boolean autoSizeColumn = false;
    private int columnWidth = 1;

    ReportSpecialColumn(SpecialColumn specialColumn) {
        this.position = specialColumn.position();
        this.title = specialColumn.title();
        this.value = specialColumn.value();
        this.format = specialColumn.format();
        this.id = specialColumn.id();
        this.valueType = specialColumn.valueType();
        this.autoSizeColumn = specialColumn.autoSizeColumn();
        this.columnWidth = specialColumn.columnWidth();
    }

    public ReportSpecialColumn(final float position, final String value) {
        this.position = position;
        this.value = value;
    }

    @Override
    public Object clone() {
        ReportSpecialColumn clone = this;
        try {
            clone = (ReportSpecialColumn) super.clone();
        } catch (CloneNotSupportedException ignored) {}
        return clone;
    }
}
