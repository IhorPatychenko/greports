package models;

import annotations.Report;
import annotations.ReportColumn;

@Report(name = "Report1")
public class BackDto {

    private int id;
    private String name;
    private String surname;

    public BackDto(int id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    @ReportColumn(reportName = "Report1", position = "1.0", title = "Messages.id")
    public int getId() {
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
}
