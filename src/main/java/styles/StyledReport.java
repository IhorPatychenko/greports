package styles;

import java.util.Map;

public interface StyledReport {
    Map<String, ReportStylesBuilder<VerticalRangedStyle>> getRangedRowStyles();
    Map<String, ReportStylesBuilder<HorizontalRangedStyle>> getRangedColumnStyles();
    Map<String, ReportStylesBuilder<PositionedStyle>> getPositionedStyles();
    Map<String, ReportStylesBuilder<RectangleRangedStyle>> getRectangleRangedStyles();
}
