package styles;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.greports.positioning.VerticalRange;
import org.greports.styles.interfaces.StyledReport;
import org.greports.styles.stylesbuilders.ReportStylesBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static models.Car.REPORT_NAME;

public interface DefaultStyles extends StyledReport {

    default Map<String, ReportStylesBuilder> getReportStyles(int rowsCount) {
        Map<String, ReportStylesBuilder> map = new HashMap<>();
        map.put(REPORT_NAME, new ReportStylesBuilder()
            .newStyle(new VerticalRange(0, null), false)
                .setBorder(BorderStyle.THIN)
                .setBorderColor(new Color(0,0,0))
            .newStyle(new VerticalRange(0, 0), true)
                .setForegroundColor(new Color(82, 78, 231, 233))
                .setFontColor(new Color(255, 255, 255))
                .setBorderColor(new Color(0,0,0))
        );
        return map;
    }

}
