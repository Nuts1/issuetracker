package com.oleksandr.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.json.Views;

import java.util.List;

public class Project {
    @JsonView({Views.Summary.class, Views.ProjectNameAndID.class})
    private long projectId;

    @JsonView({Views.Summary.class, Views.ProjectDateAndName.class, Views.ProjectNameAndID.class, Views.ProjectForAdmin.class})
    private String name;

    @JsonView({Views.Summary.class, Views.ProjectForAdmin.class})
    private java.sql.Date startDate;

    @JsonView({Views.Summary.class, Views.ProjectForAdmin.class})
    private java.sql.Date completionDate;

    @JsonView({Views.ProjectDateAndName.class, Views.Summary.class, Views.ProjectForAdmin.class})
    private java.sql.Date predicatedCompletionDate;

    @JsonView({Views.Summary.class, Views.ProjectForAdmin.class})
    private Employee customer;

    @JsonView({Views.Summary.class, Views.ProjectForAdmin.class})
    private Employee manager;

    @JsonView(Views.Summary.class)
    private List<Sprint> sprints;

    public List<Sprint> getSprints() {
        return sprints;
    }

    public void setSprints(List<Sprint> sprints) {
        this.sprints = sprints;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
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

    public java.sql.Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(java.sql.Date completionDate) {
        this.completionDate = completionDate;
    }

    public java.sql.Date getPredicatedCompletionDate() {
        return predicatedCompletionDate;
    }

    public void setPredicatedCompletionDate(java.sql.Date predicatedCompletionDate) {
        this.predicatedCompletionDate = predicatedCompletionDate;
    }

    public Employee getCustomer() {
        return customer;
    }

    public void setCustomer(Employee customer) {
        this.customer = customer;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }
}