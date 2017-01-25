package com.oleksandr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.sql.Date;

/**
 * Created by Nuts on 1/11/2017
 * 4:30 PM.
 */
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ProjectDto {
    private String projectId;
    private String name;
    private Date startDate;
    private Date completionDate;
    private String managerId;
    private String customerId;

    public String getName() {
        return name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
