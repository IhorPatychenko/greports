package org.greports.positioning;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Range<T extends Serializable, E extends Serializable> implements Serializable {

    private static final long serialVersionUID = 8980520714685635759L;
    private T start;
    private E end;

    public abstract RectangleRange toRectangleRange();
}
