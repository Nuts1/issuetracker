package com.oleksandr.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.json.Views;

import java.util.List;

public class Sprint {

    @JsonView({Views.Summary.class, Views.SprintDateIdPrevIdAndName.class})
    private long sprintId;

    @JsonView({Views.Summary.class, Views.SprintDateIdPrevIdAndName.class})
    private String name;

    @JsonView({Views.Summary.class, Views.SprintDateIdPrevIdAndName.class})
    private java.sql.Date startDate;

    @JsonView(Views.Summary.class)
    private Long previousSprint;

    @JsonView({Views.Summary.class, Views.SprintDateIdPrevIdAndName.class})
    private java.sql.Date completionDate;

    @JsonView(Views.Summary.class)
    private List<Task> tasks;

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> t) {
        this.tasks = t;
    }


    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public java.sql.Date getStartDate() {
        return startDate;
    }

    public void setStartDate(java.sql.Date startDate) {
        this.startDate = startDate;
    }

    public Long getPreviousSprint() {
        return previousSprint;
    }

    public void setPreviousSprint(Long previousSprint) {
        this.previousSprint = previousSprint;
    }

    public java.sql.Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(java.sql.Date completionDate) {
        this.completionDate = completionDate;
    }

}