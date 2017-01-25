package com.oleksandr.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.json.Views;

import java.util.Date;
import java.util.List;

public class Task {
    @JsonView({Views.Summary.class, Views.TaskEmployee.class})
    private long taskId;

    @JsonView({Views.Summary.class, Views.TaskEmployee.class})
    private String name;

    @JsonView({Views.Summary.class, Views.TaskEmployee.class})
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm", timezone="EET")
    private java.util.Date startDate;

    @JsonView({Views.Summary.class, Views.TaskEmployee.class})
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm", timezone="EET")
    private java.util.Date completionDate;

    @JsonView({Views.Summary.class, Views.TaskEmployee.class})
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm", timezone="EET")
    private java.util.Date actualCompletionDate;

    @JsonView({Views.Summary.class, Views.TaskEmployee.class})
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm", timezone="EET")
    private java.util.Date actualStartDate;

    @JsonView({Views.Summary.class, Views.TaskEmployee.class})
    private int estimate;

    @JsonView({Views.Summary.class, Views.TaskEmployee.class})
    private String predictedDelay;

    @JsonView({Views.Summary.class, Views.TaskEmployee.class})
    private long subtask;

    @JsonView({Views.Summary.class, Views.TaskEmployee.class})
    private Task previousTask;

    @JsonView({Views.Summary.class, Views.TaskEmployee.class})
    private String description;

    @JsonView(Views.Summary.class)
    private List<Employee> employees;

    public Date getActualCompletionDate() {
        return actualCompletionDate;
    }

    public void setActualCompletionDate(Date actualCompletionDate) {
        this.actualCompletionDate = actualCompletionDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public String getPredictedDelay() {
        return predictedDelay;
    }

    public void setPredictedDelay(String predictedDelay) {
        this.predictedDelay = predictedDelay;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public java.util.Date getStartDate() {
        return startDate;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    public int getEstimate() {
        return estimate;
    }

    public void setEstimate(int estimate) {
        this.estimate = estimate;
    }

    public long getSubtask() {
        return subtask;
    }

    public void setSubtask(long subtask) {
        this.subtask = subtask;
    }

    public Task getPreviousTask() {
        return previousTask;
    }

    public void setPreviousTask(Task previousTask) {
        this.previousTask = previousTask;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(Date actualStartDate) {
        this.actualStartDate = actualStartDate;
    }
}