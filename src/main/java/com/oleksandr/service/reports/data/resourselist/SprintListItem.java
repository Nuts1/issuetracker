package com.oleksandr.service.reports.data.resourselist;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.json.Views;

import java.util.List;

/**
 * Created by Nuts on 1/18/2017
 * 4:01 PM.
 */
public class SprintListItem {
    @JsonView(Views.Summary.class)
    String name;

    @JsonView(Views.Summary.class)
    private List<DayWork> totalDayWorks; // total hours work on specific day and specific Sprint

    @JsonView(Views.Summary.class)
    private List<TaskItem> taskItems;

    public List<TaskItem> getTaskItems() {
        return taskItems;
    }

    public void setTaskItems(List<TaskItem> taskItems) {
        this.taskItems = taskItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DayWork> getTotalDayWorks() {
        return totalDayWorks;
    }

    public void setTotalDayWorks(List<DayWork> totalDayWorks) {
        this.totalDayWorks = totalDayWorks;
    }
}
