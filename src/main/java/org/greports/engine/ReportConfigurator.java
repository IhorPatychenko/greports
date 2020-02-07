package org.greports.engine;

import java.util.HashMap;
import java.util.Map;

public class ReportConfigurator {

    private Map<Integer, String> overriddenTitles = new HashMap<>();

    public Map<Integer, String> getOverriddenTitles() {
        return overriddenTitles;
    }

    public ReportConfigurator setOverriddenTitles(final Map<Integer, String> overriddenTitles) {
        this.overriddenTitles = overriddenTitles;
        return this;
    }

    public ReportConfigurator overrideTitle(final Integer columnIndex, final String title) {
        this.overriddenTitles.put(columnIndex, title);
        return this;
    }
}
