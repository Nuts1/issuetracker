package com.oleksandr.service.entity.impl;

import com.oleksandr.dao.TaskDao;
import com.oleksandr.dto.TaskDto;
import com.oleksandr.entity.Task;
import com.oleksandr.service.entity.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Nuts on 1/13/2017
 * 12:56 PM.
 */
@Service
public class TaskServiceImpl implements TaskService {
    private final TaskDao dao;

    @Autowired
    public TaskServiceImpl(TaskDao taskDao) {
        this.dao = taskDao;
    }

    @Override
    public List<Task> getBySprintId(long id) {
        return dao.getBySprintId(id);
    }

    @Override
    public Task getById(long id) {
        return dao.getById(id);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public void save(TaskDto taskWithResourcesDto) {
        dao.save(taskWithResourcesDto);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public void delete(long id) {
        List<TaskDto> tasks = dao.getAllDependentTaskDto(id); // dependentTask.previous_task_id = id;
        if(tasks != null) {
            TaskDto taskDto = dao.getDtoById(id);
            tasks.forEach(e -> {
                e.setPreviousTaskId(taskDto.getPreviousTaskId());
                dao.update(e);
            });
        }
        
        dao.delete(id);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public int update(TaskDto taskWithResourcesDto) {
        return dao.update(taskWithResourcesDto);
    }

    @Override
    public List<Task> getByEmployeeId(long employeeId) {
        return dao.getByEmployeeId(employeeId);
    }

    @Override
    public String complete(long taskId, long employeeId) {
        Task task = dao.getById(taskId);
        if(task.getActualCompletionDate() != null) {
            return "Already complete";
        }
        if(task.getPreviousTask() != null) {
            Date previousTaskCompletion = task.getPreviousTask().getActualCompletionDate();
            if (previousTaskCompletion == null) {
                return "Previous task not complete";
            } else {
                Calendar calendar = Calendar.getInstance();
                dao.setActualCompleteDate(taskId, calendar.getTime());
                return "Success";
            }
        } else {
            Calendar calendar = Calendar.getInstance();
            dao.setActualCompleteDate(taskId, calendar.getTime());
            return "Success";
        }
    }

    @Override
    public String setDelay(long id, long d) {
        if(dao.setDelay(id, d)) {
            return "Success";
        } else {
            return "Error";
        }
    }
}
