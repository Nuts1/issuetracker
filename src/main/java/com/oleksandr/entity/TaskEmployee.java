package com.oleksandr.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.json.Views;

public class TaskEmployee {
    @JsonView(Views.Summary.class)
    private long idTaskEmployee;
    @JsonView(Views.Summary.class)
    private Employee employee;
    @JsonView({Views.Summary.class, Views.TaskEmployee.class})
    private int load;

    @JsonView({Views.Summary.class, Views.TaskEmployee.class})
    private Task task;

    @JsonView({Views.Summary.class, Views.TaskEmployee.class})
    private String projectName;

    @JsonView({Views.Summary.class, Views.TaskEmployee.class})
    private String sprintName;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @JsonView({Views.Summary.class, Views.TaskEmployee.class})
    private String confirm;

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public long getIdTaskEmployee() {
        return idTaskEmployee;
    }

    public void setIdTaskEmployee(long idTaskEmployee) {
        this.idTaskEmployee = idTaskEmployee;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    @Override
    public String toString() {
        return "TaskEmployee{" +
                "idTaskEmployee=" + idTaskEmployee +
                ", employee=" + employee +
                ", load=" + load +
                ", task=" + task +
                ", projectName='" + projectName + '\'' +
                ", sprintName='" + sprintName + '\'' +
                ", confirm='" + confirm + '\'' +
                '}';
    }
}
