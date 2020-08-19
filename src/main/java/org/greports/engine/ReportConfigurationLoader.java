package org.greports.engine;

import org.greports.utils.AnnotationUtils;

public class ReportConfigurationLoader {

    private ReportConfigurationLoader() {
    }

    public static ReportConfiguration load(String className, String reportName) throws ClassNotFoundException {
        return load(Class.forName(className), reportName);
    }

    public static ReportConfiguration load(Class<?> clazz, String reportName) {
        return new ReportConfiguration(AnnotationUtils.getReportConfiguration(clazz, reportName));
    }
}
