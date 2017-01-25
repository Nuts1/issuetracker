package com.oleksandr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.json.Views;

import java.sql.Date;

/**
 * Created by Nuts on 1/11/2017
 * 10:50 PM.
 */
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class SprintDto {
    @JsonView({Views.Summary.class, Views.SprintDateIdPrevIdAndName.class})
    private long sprintId;

    @JsonView({Views.Summary.class, Views.SprintDateIdPrevIdAndName.class})
    private String name;

    @JsonView({Views.Summary.class, Views.SprintDateIdPrevIdAndName.class})
    private java.sql.Date startDate;

    @JsonView({Views.Summary.class, Views.SprintDateIdPrevIdAndName.class})
    private String previousSprint;

    @JsonView({Views.Summary.class, Views.SprintDateIdPrevIdAndName.class})
    private java.sql.Date completionDate;

    @JsonView({Views.Summary.class, Views.SprintDateIdPrevIdAndName.class})
    private long projectId;

    public long getSprintId() {
        return sprintId;
    }

    public void setSprintId(long sprintId) {
        this.sprintId = sprintId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getPreviousSprint() {
        return previousSprint;
    }

    public void setPreviousSprint(String previousSprint) {
        this.previousSprint = previousSprint;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "SprintDto{" +
                "sprintId=" + sprintId +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", previousSprint=" + previousSprint +
                ", completionDate=" + completionDate +
                ", projectId=" + projectId +
                '}';
    }
}
