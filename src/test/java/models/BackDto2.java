package models;

import annotations.Report;
import annotations.ReportColumn;
import annotations.ReportSpecialCell;
import annotations.ReportSpecialCell.ValueType;
import annotations.ReportSpecialRow;
import formula.Formulas;
import org.apache.poi.ss.usermodel.IndexedColors;
import styles.HorizontalRangedStyle;
import styles.PositionedStyle;
import styles.RectangleRangedStyle;
import styles.ReportStylesBuilder;
import styles.ReportStylesBuilder.StylePriority;
import styles.interfaces.StripedRows;
import styles.interfaces.StyledReport;
import styles.VerticalRangedStyle;
import positioning.VerticalRange;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Report(
        name = "Report1",
        translationsDir = "src/test/resources/i18n/",
        headerOffset = 1,
        dataOffset = 2,
        sortableHeader = true,
        emptyColumns = {
                @ReportColumn(reportName = "Report1", position = "3.5", title = "Messages.emptyColumn")
        },
        specialRows = {
                @ReportSpecialRow(
                        rowIndex = 0,
                        columns = {
                                @ReportSpecialCell(targetId = "id", valueType = ValueType.FORMULA, value = Formulas.SUMA)
                        }
                )
        }
)
public class BackDto2 implements StyledReport, StripedRows {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    private Integer id;
    private String name;
    private String surname;
    private Date birthDay;

    public BackDto2(Integer id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthDay = new Date();
    }

    @ReportColumn(reportName = "Report1", position = "2.1", title = "Messages.id", id = "id")
    public Integer getId() {
        return id;
    }

    @ReportColumn(reportName = "Report1", position = "2.0", title = "Messages.name")
    public String getName() {
        return name;
    }

    @ReportColumn(reportName = "Report1", position = "3.0", title = "Messages.surname")
    public String getSurname() {
        return surname;
    }

    @ReportColumn(reportName = "Report1", position = "4.0", title = "Messages.birthDay", format = DATE_FORMAT)
    public Date getBirthDay(){
        return birthDay;
    }

    @ReportColumn(reportName = "Report1", position = "5.0", title = "Boolean")
    public boolean getBoolean(){
        return true;
    }

    @Override
    public Map<String, ReportStylesBuilder<VerticalRangedStyle>> getRangedRowStyles() {
        return new HashMap<String, ReportStylesBuilder<VerticalRangedStyle>>() {{
            put("Report2", new ReportStylesBuilder<>(VerticalRangedStyle.class, StylePriority.PRIORITY1)
                    .newStyle(new VerticalRange(4, 4))
                    .setForegroundColor(IndexedColors.GOLD)
                    .parent()
            );
        }};
    }

    @Override
    public Map<String, ReportStylesBuilder<HorizontalRangedStyle>> getRangedColumnStyles() {
        return null;
    }

    @Override
    public Map<String, ReportStylesBuilder<PositionedStyle>> getPositionedStyles() {
        return null;
    }

    @Override
    public Map<String, ReportStylesBuilder<RectangleRangedStyle>> getRectangleRangedStyles() {
        return null;
    }

    @Override
    public StripedRowsIndex getStripedRowsIndex() {
        return StripedRowsIndex.EVEN;
    }

    @Override
    public IndexedColors getStripedRowsColor() {
        return IndexedColors.GREY_25_PERCENT;
    }
}
