package org.greports.engine;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.util.HashMap;
import java.util.Map;

public class CellStylesBuilder {

    private final Map<String, Object> _map = new HashMap<>();

    public CellStylesBuilder() {
        // Default values
        _map.put("fillPattern", FillPatternType.SOLID_FOREGROUND);
    }

    public CellStylesBuilder setFontName(String fontName) {
        _map.put("font", fontName);
        return this;
    }

    public CellStylesBuilder setForegroundColor(IndexedColors foregroundColor) {
        _map.put("fillForegroundColor", foregroundColor.index);
        return this;
    }

    public CellStylesBuilder setBackgroundColor(IndexedColors backgroundColor) {
        _map.put("fillBackgroundColor", backgroundColor.index);
        return this;
    }

    public CellStylesBuilder setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        _map.put("alignment", horizontalAlignment);
        return this;
    }

    public CellStylesBuilder setVerticalAlignment(VerticalAlignment verticalAlignment) {
        _map.put("verticalAlignment", verticalAlignment);
        return this;
    }

    public CellStylesBuilder setBorder(BorderStyle border) {
        setBorderTop(border);
        setBorderBottom(border);
        setBorderLeft(border);
        setBorderRight(border);
        return this;
    }

    public CellStylesBuilder setBorderTop(BorderStyle borderTop) {
        _map.put("borderTop", borderTop);
        return this;
    }

    public CellStylesBuilder setBorderRight(BorderStyle borderRight) {
        _map.put("borderRight", borderRight);
        return this;
    }

    public CellStylesBuilder setBorderBottom(BorderStyle borderBottom) {
        _map.put("borderBottom", borderBottom);
        return this;
    }

    public CellStylesBuilder setBorderLeft(BorderStyle borderLeft) {
        _map.put("borderLeft", borderLeft);
        return this;
    }

    public CellStylesBuilder setLeftBorderColor(IndexedColors color) {
        _map.put("leftBorderColor", color.index);
        return this;
    }

    public CellStylesBuilder setRightBorderColor(IndexedColors color) {
        _map.put("rightBorderColor", color.index);
        return this;
    }

    public CellStylesBuilder setTopBorderColor(IndexedColors color) {
        _map.put("topBorderColor", color.index);
        return this;
    }

    public CellStylesBuilder setBottomBorderColor(IndexedColors color) {
        _map.put("bottomBorderColor", color.index);
        return this;
    }

    public CellStylesBuilder setHidden(Boolean hidden) {
        _map.put("hidden", hidden);
        return this;
    }

    public CellStylesBuilder setIndentation(Short indentation) {
        _map.put("indention", indentation);
        return this;
    }

    public CellStylesBuilder setLocked(Boolean locked) {
        _map.put("locked", locked);
        return this;
    }

    public CellStylesBuilder setRotation(Short rotation) {
        _map.put("rotation", rotation);
        return this;
    }

    public CellStylesBuilder setFillPattern(FillPatternType fillPattern) {
        _map.put("fillPattern", fillPattern);
        return this;
    }

    public CellStylesBuilder setBorderColor(IndexedColors color) {
        setLeftBorderColor(color);
        setRightBorderColor(color);
        setTopBorderColor(color);
        setBottomBorderColor(color);
        return this;
    }

    public CellStylesBuilder setWrapText(Boolean wrapText) {
        _map.put("wrapText", wrapText);
        return this;
    }

    public CellStylesBuilder setDataFormat(String format) {
        _map.put("dataFormat", format);
        return this;
    }

    Map<String, Object> build() {
        return this._map;
    }

}
