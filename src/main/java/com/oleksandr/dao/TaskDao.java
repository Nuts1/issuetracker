package com.oleksandr.dao;

import com.oleksandr.dto.TaskDto;
import com.oleksandr.entity.Task;

import java.util.Date;
import java.util.List;

/**
 * Created by Nuts on 1/5/2017
 * 8:58 PM.
 */
public interface TaskDao {
    List<com.oleksandr.entity.Task> getBySprintId(long id);

    com.oleksandr.entity.Task getById(long id);

    boolean save(TaskDto taskWithResourcesDto);

    void delete(long id);

    int update(TaskDto taskWithResourcesDto);

    List<com.oleksandr.entity.Task> getByEmployeeId(long employeeId);

    void setActualCompleteDate(long taskId, Date time);

    boolean setDelay(long id, long d);

    com.oleksandr.entity.Task getByIdWithoutEmployees(long task_id);
}
