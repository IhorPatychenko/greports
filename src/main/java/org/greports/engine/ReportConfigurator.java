package org.greports.engine;

import org.greports.exceptions.ReportEngineRuntimeException;

import java.util.HashMap;
import java.util.Map;

public class ReportConfigurator {

    private Map<Integer, String> overriddenTitles = new HashMap<>();
    private String sheetName;

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

    public ReportConfigurator setSheetName(final String newName) {
        if(newName == null){
            throw new ReportEngineRuntimeException("newName cannot be null", this.getClass());
        }
        this.sheetName = newName;
        return this;
    }

    public String getSheetName() {
        return sheetName;
    }
}
