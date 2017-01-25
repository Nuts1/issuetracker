package com.oleksandr.service.reports.data.resourselist;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.json.Views;

import java.util.List;

public class EmployeeListItem {
    @JsonView(Views.Summary.class)
    private String name;
    @JsonView(Views.Summary.class)
    private double totalWorks;
    @JsonView(Views.Summary.class)
    private List<DayWork> totalDayWorks; // total hours work on specific day
    @JsonView(Views.Summary.class)
    private List<ProjectListItem> projectListItems;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotalWorks() {
        return totalWorks;
    }

    public void setTotalWorks(double totalWorks) {
        this.totalWorks = totalWorks;
    }

    public List<DayWork> getTotalDayWorks() {
        return totalDayWorks;
    }

    public void setTotalDayWorks(List<DayWork> totalDayWorks) {
        this.totalDayWorks = totalDayWorks;
    }

    public List<ProjectListItem> getProjectListItems() {
        return projectListItems;
    }

    public void setProjectListItems(List<ProjectListItem> projectListItems) {
        this.projectListItems = projectListItems;
    }
}
