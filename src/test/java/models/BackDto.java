package models;

import annotations.Report;
import annotations.ReportColumn;
import org.apache.poi.ss.usermodel.IndexedColors;
import styles.HorizontalRangedStyle;
import styles.PositionedStyle;
import styles.RectangleRangedStyle;
import styles.ReportStylesBuilder;
import styles.ReportStylesBuilder.StylePriority;
import styles.StyledReport;
import styles.VerticalRangedStyle;
import utils.HorizontalRange;
import utils.Position;
import utils.RectangleRange;
import utils.VerticalRange;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Report(
    name = "Report1",
    translationsDir = "src/test/resources/i18n/",
    emptyColumns = {
        @ReportColumn(reportName = "Report1", position = "3.5", title = "Messages.emptyColumn")
    }
)
public class BackDto implements StyledReport {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    private Integer id;
    private String name;
    private String surname;
    private Date birthDay;

    public BackDto(Integer id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthDay = new Date();
    }

    @ReportColumn(reportName = "Report1", position = "1.0", title = "Messages.id")
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
            put("Report1", new ReportStylesBuilder<>(VerticalRangedStyle.class, StylePriority.PRIORITY1)
                    .newStyle(new VerticalRange(0, 0))
                    .setForegroundColor(IndexedColors.AQUA)
                    .parent()
                    .newStyle(new VerticalRange(1, 2))
                    .setBoldFont(true)
                    .parent()
            );
        }};
    }

    @Override
    public Map<String, ReportStylesBuilder<HorizontalRangedStyle>> getRangedColumnStyles() {
//        return new HashMap<String, ReportStylesBuilder<HorizontalRangedStyle>>() {{
////            put("Report1", new ReportStylesBuilder<>(HorizontalRangedStyle.class, StylePriority.MEDIUM)
////                    .newStyle()
////                    .setBackgroundColor((short) 11)
////                    .setBoldFont(true)
////                    .parent());
//        }};
        return null;
    }

    @Override
    public Map<String, ReportStylesBuilder<PositionedStyle>> getPositionedStyles() {
        return new HashMap<String, ReportStylesBuilder<PositionedStyle>>() {{
            put("Report1", new ReportStylesBuilder<>(PositionedStyle.class, StylePriority.PRIORITY3)
                    .newStyle(new Position(0,1))
                    .setForegroundColor(IndexedColors.BLUE)
                    .setFontColor(IndexedColors.WHITE)
                    .parent());
        }};
    }

    @Override
    public Map<String, ReportStylesBuilder<RectangleRangedStyle>> getRectangleRangedStyles() {
        return new HashMap<String, ReportStylesBuilder<RectangleRangedStyle>>() {{
            put("Report1", new ReportStylesBuilder<>(RectangleRangedStyle.class, StylePriority.PRIORITY2)
                    .newStyle(new RectangleRange(new VerticalRange(1, 2), new HorizontalRange(1, 3)))
                    .setForegroundColor(IndexedColors.CORAL)
                    .parent());
        }};
    }
}
