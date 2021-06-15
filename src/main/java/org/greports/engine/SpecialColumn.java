package org.greports.engine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class SpecialColumn implements Cloneable, Serializable {
    private static final long serialVersionUID = -4461036741398777369L;

    private float position;
    private String title = "";
    private String value;
    private String format = "";
    private String id = "";
    private ValueType valueType = ValueType.PLAIN_VALUE;
    private boolean autoSizeColumn = false;
    private int columnWidth = 1;

    SpecialColumn(org.greports.annotations.SpecialColumn specialColumn) {
        this.position = specialColumn.position();
        this.title = specialColumn.title();
        this.value = specialColumn.value();
        this.format = specialColumn.format();
        this.id = specialColumn.id();
        this.valueType = specialColumn.valueType();
        this.autoSizeColumn = specialColumn.autoSizeColumn();
        this.columnWidth = specialColumn.columnWidth();
    }

    public SpecialColumn(final float position, final String value) {
        this.position = position;
        this.value = value;
    }

    @Override
    public Object clone() {
        SpecialColumn clone = this;
        try {
            clone = (SpecialColumn) super.clone();
        } catch (CloneNotSupportedException ignored) {}
        return clone;
    }
}
