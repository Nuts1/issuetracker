package com.oleksandr.service.reports.data.resourselist;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.json.Views;

import java.util.List;

public class TaskItem {
    @JsonView(Views.Summary.class)
    private String name;

    @JsonView(Views.Summary.class)
    private List<DayWork> dayWorks;   // total hours work on specific day and specific task

    @JsonView(Views.Summary.class)
    int load;

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DayWork> getDayWorks() {
        return dayWorks;
    }

    public void setDayWorks(List<DayWork> dayWorks) {
        this.dayWorks = dayWorks;
    }
}
