package com.oleksandr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;


import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nuts on 1/7/2017
 * 3:51 PM.
 */
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class TaskDto {
    private Long idTask;
    private String name;
    private Date startDate;
    private Date completionDate;
    private int startTime;
    private int completionTime;
    private Date actualCompletionDate;
    private Date actualStartDate;
    private int estimate;
    private int predictedDelay;
    private String previousTaskId;
    private long sprintId;
    private String description;
    private String employeeIds;
    private String employeeUnits;
    private Integer load;

    public Integer getLoad() {
        return load;
    }

    public void setLoad(Integer load) {
        this.load = load;
    }

    public Date getActualCompletionDate() {
        return actualCompletionDate;
    }

    public void setActualCompletionDate(Date actualCompletionDate) {
        this.actualCompletionDate = actualCompletionDate;
    }

    public Date getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(Date actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public String getEmployeeUnits() {
        return employeeUnits;
    }

    public void setEmployeeUnits(String employeeUnits) {
        this.employeeUnits = employeeUnits;
    }

    public TaskDto() {
    }

    public Long getIdTask() {
        return idTask;
    }

    public void setIdTask(Long idTask) {
        this.idTask = idTask;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.set(Calendar.HOUR, startTime);
        return cal.getTime();
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getCompletionDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(completionDate);
        cal.set(Calendar.HOUR, completionTime);
        return cal.getTime();
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public int getEstimate() {
        return estimate;
    }

    public void setEstimate(int estimate) {
        this.estimate = estimate;
    }

    public int getPredictedDelay() {
        return predictedDelay;
    }

    public void setPredictedDelay(int predictedDelay) {
        this.predictedDelay = predictedDelay;
    }

    public String getPreviousTaskId() {
        return previousTaskId;
    }

    public void setPreviousTaskId(String previousTaskId) {
        this.previousTaskId = previousTaskId;
    }

    public long getSprintId() {
        return sprintId;
    }

    public void setSprintId(long sprintId) {
        this.sprintId = sprintId;
    }

    public String getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(String employeeIds) {
        this.employeeIds = employeeIds;
    }

    public Long[] getIds() {
        if(employeeIds != null) {
            String[] strings = employeeIds
                    .substring(1, employeeIds.length() - 1)
                    .split(",");
            Long[] ids = new Long[strings.length];
            for (int i = 0; i < strings.length; i++) {
                try {
                    ids[i] = Long.parseLong(strings[i]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return ids;
        } else {
            return null;
        }
    }

    public Integer[] getUnits() {
        if (employeeUnits != null) {
            String[] strings = employeeUnits
                    .substring(1, employeeUnits.length() - 1)
                    .split(",");
            Integer[] ids = new Integer[strings.length];
            for (int i = 0; i < strings.length; i++) {
                try {
                    ids[i] = Integer.parseInt(strings[i]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return ids;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "TaskDto{" +
                "name='" + name + '\'' +
                ", startDate=" + startDate +
                ", completionDate=" + completionDate +
                ", estimate=" + estimate +
                ", predictedDelay=" + predictedDelay +
                ", previousTaskId=" + previousTaskId +
                ", sprintId=" + sprintId +
                ", employeeIds=" + employeeIds +
                ", employeeIds=" + employeeUnits +
                '}';
    }
}
