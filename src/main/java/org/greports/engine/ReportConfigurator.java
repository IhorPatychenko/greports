package org.greports.engine;

import org.greports.exceptions.ReportEngineRuntimeException;

import java.util.HashMap;
import java.util.Map;

public class ReportConfigurator {

    private ReportGenerator reportGenerator;

    private Map<Integer, String> overriddenTitles = new HashMap<>();
    private String sheetName;
    private Map<Class<?>, String> formats = new HashMap<>();

    protected ReportGenerator getReportGenerator() {
        return reportGenerator;
    }

    protected ReportConfigurator(final ReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
    }

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
            throw new ReportEngineRuntimeException("newName parameter cannot be null", this.getClass());
        }
        this.sheetName = newName;
        return this;
    }

    public String getSheetName() {
        return sheetName;
    }

    public ReportConfigurator setFormatForClass(final Class<?> clazz, final String format) {
        if(clazz == null || format == null){
            throw new ReportEngineRuntimeException("clazz and format parameters cannot be null", this.getClass());
        }
        this.formats.put(clazz, format);
        return this;
    }

    public String getFormatForClass(final Class<?> clazz) {
        if(clazz == null){
            throw new ReportEngineRuntimeException("clazz parameter cannot be null", this.getClass());
        }
        return this.formats.getOrDefault(clazz, "");
    }
}
