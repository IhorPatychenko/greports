package org.greports.engine;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellUtil;

import java.util.HashMap;
import java.util.Map;

public class CellStylesBuilder {

    private final Map<String, Object> _map = new HashMap<>();

    public CellStylesBuilder() {
        // Default values
        _map.put(CellUtil.FILL_PATTERN, FillPatternType.SOLID_FOREGROUND);
    }

    public CellStylesBuilder setFontName(String fontName) {
        _map.put(CellUtil.FONT, fontName);
        return this;
    }

    public CellStylesBuilder setForegroundColor(IndexedColors foregroundColor) {
        _map.put(CellUtil.FILL_FOREGROUND_COLOR, foregroundColor.index);
        return this;
    }

    public CellStylesBuilder setBackgroundColor(IndexedColors backgroundColor) {
        _map.put(CellUtil.FILL_BACKGROUND_COLOR, backgroundColor.index);
        return this;
    }

    public CellStylesBuilder setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        _map.put(CellUtil.ALIGNMENT, horizontalAlignment);
        return this;
    }

    public CellStylesBuilder setVerticalAlignment(VerticalAlignment verticalAlignment) {
        _map.put(CellUtil.VERTICAL_ALIGNMENT, verticalAlignment);
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
        _map.put(CellUtil.BORDER_TOP, borderTop);
        return this;
    }

    public CellStylesBuilder setBorderRight(BorderStyle borderRight) {
        _map.put(CellUtil.BORDER_RIGHT, borderRight);
        return this;
    }

    public CellStylesBuilder setBorderBottom(BorderStyle borderBottom) {
        _map.put(CellUtil.BORDER_BOTTOM, borderBottom);
        return this;
    }

    public CellStylesBuilder setBorderLeft(BorderStyle borderLeft) {
        _map.put(CellUtil.BORDER_LEFT, borderLeft);
        return this;
    }

    public CellStylesBuilder setLeftBorderColor(IndexedColors color) {
        _map.put(CellUtil.LEFT_BORDER_COLOR, color.index);
        return this;
    }

    public CellStylesBuilder setRightBorderColor(IndexedColors color) {
        _map.put(CellUtil.RIGHT_BORDER_COLOR, color.index);
        return this;
    }

    public CellStylesBuilder setTopBorderColor(IndexedColors color) {
        _map.put(CellUtil.TOP_BORDER_COLOR, color.index);
        return this;
    }

    public CellStylesBuilder setBottomBorderColor(IndexedColors color) {
        _map.put(CellUtil.BOTTOM_BORDER_COLOR, color.index);
        return this;
    }

    public CellStylesBuilder setHidden(Boolean hidden) {
        _map.put(CellUtil.HIDDEN, hidden);
        return this;
    }

    public CellStylesBuilder setIndentation(Short indention) {
        _map.put(CellUtil.INDENTION, indention);
        return this;
    }

    public CellStylesBuilder setLocked(Boolean locked) {
        _map.put(CellUtil.LOCKED, locked);
        return this;
    }

    public CellStylesBuilder setRotation(Short rotation) {
        _map.put(CellUtil.ROTATION, rotation);
        return this;
    }

    public CellStylesBuilder setFillPattern(FillPatternType fillPattern) {
        _map.put(CellUtil.FILL_PATTERN, fillPattern);
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
        _map.put(CellUtil.WRAP_TEXT, wrapText);
        return this;
    }

    public CellStylesBuilder setDataFormat(String format) {
        _map.put(CellUtil.DATA_FORMAT, format);
        return this;
    }

    Map<String, Object> build() {
        return this._map;
    }

}
