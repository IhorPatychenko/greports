package org.greports.styles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.greports.positioning.RectangleRange;
import org.greports.styles.interfaces.StripedRows;

import java.awt.*;
import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReportStyle implements Serializable {
    private static final long serialVersionUID = 4183157194410162170L;

    private RectangleRange range;
    private String fontName;
    private Color foregroundColor;
    private Color fontColor;
    private FillPatternType fillPattern;
    private Short fontSize;
    private Boolean boldFont;
    private Boolean italicFont;
    private FontUnderline underlineFont;
    private Boolean strikeoutFont;
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;
    private BorderStyle borderTop;
    private BorderStyle borderBottom;
    private BorderStyle borderLeft;
    private BorderStyle borderRight;
    private Color borderColor;
    private StripedRows.StripedRowsIndex stripedRowsIndex;
    private Color stripedRowsColor;
    private boolean clonePreviousStyle;
    private Color leftBorderColor;
    private Color rightBorderColor;
    private Color topBorderColor;
    private Color bottomBorderColor;
    private Boolean hidden;
    private Short indentation;
    private Boolean locked;
    private Boolean quotePrefixed;
    private Short rotation;
    private Boolean shrinkToFit;
    private Float rowHeight;
    private Integer columnWidth;
    private Boolean wrapText;

    public ReportStyle(RectangleRange range){
        this.range = range;
    }

}
