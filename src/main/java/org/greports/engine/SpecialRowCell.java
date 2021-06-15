package org.greports.engine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class SpecialRowCell implements Cloneable, Serializable {
    private static final long serialVersionUID = -4555194088608860976L;

    private String targetId;
    private ValueType valueType;
    private String value;
    private String format;
    private String comment;
    private short commentWidth;
    private short commentHeight;
    private int columnWidth;

    SpecialRowCell(org.greports.annotations.SpecialRowCell specialRowCell) {
        this(specialRowCell.targetId(),
            specialRowCell.valueType(),
            specialRowCell.value(),
            specialRowCell.format(),
            specialRowCell.comment(),
            specialRowCell.commentWidth(),
            specialRowCell.commentHeight(),
            specialRowCell.columnWidth()
        );
    }

    @Override
    public Object clone() {
        SpecialRowCell clone = this;
        try {
            clone = (SpecialRowCell) super.clone();
        } catch (CloneNotSupportedException ignored) {}
        return clone;
    }
}
