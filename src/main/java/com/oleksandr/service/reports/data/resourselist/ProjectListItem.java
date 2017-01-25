package com.oleksandr.service.reports.data.resourselist;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.json.Views;

import java.util.List;

/**
 * Created by Nuts on 1/18/2017
 * 4:00 PM.
 */
public class ProjectListItem {
    @JsonView(Views.Summary.class)
    private List<DayWork> totalDayWorks; // total hours work on specific day and specific project

    @JsonView(Views.Summary.class)
    String nameProject;

    @JsonView(Views.Summary.class)
    private List<SprintListItem> sprintListItems; // total hours work on specific day and on specific task

    public String getNameProject() {
        return nameProject;
    }

    public void setNameProject(String nameProject) {
        this.nameProject = nameProject;
    }

    public List<DayWork> getTotalDayWorks() {
        return totalDayWorks;
    }

    public void setTotalDayWorks(List<DayWork> totalDayWorks) {
        this.totalDayWorks = totalDayWorks;
    }

    public List<SprintListItem> getSprintListItems() {
        return sprintListItems;
    }

    public void setSprintListItems(List<SprintListItem> sprintListItems) {
        this.sprintListItems = sprintListItems;
    }
}
