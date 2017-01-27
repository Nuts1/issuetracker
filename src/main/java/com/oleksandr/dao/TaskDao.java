package com.oleksandr.dao;

import com.oleksandr.dto.TaskDto;
import com.oleksandr.entity.Task;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Nuts on 1/5/2017
 * 8:58 PM.
 */
public interface TaskDao {
    List<Task> getBySprintId(long id);

    Task getById(long id);

    boolean save(TaskDto taskWithResourcesDto);

    void delete(long id);

    int update(TaskDto taskWithResourcesDto);

    List<Task> getByEmployeeId(long employeeId);

    void setActualCompleteDate(long taskId, Date time);

    boolean setDelay(long id, long d);

    Task getByIdWithoutEmployees(long task_id);

    List<TaskDto> getAllDependentTaskDto(long id);

    TaskDto getDtoById(long id);

    void setActualCompletionDate(Long idTask, Date time);

    void setActualStartDate(Long idTask, Date time);
}
