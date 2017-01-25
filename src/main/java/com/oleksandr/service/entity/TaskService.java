package com.oleksandr.service.entity;

import com.oleksandr.dto.TaskDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Created by nuts on 24.01.17.
 */
public interface TaskService {
    List<com.oleksandr.entity.Task> getBySprintId(long id);

    com.oleksandr.entity.Task getById(long id);

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    void save(TaskDto taskWithResourcesDto);

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    void delete(long id);

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    int update(TaskDto taskWithResourcesDto);

    List<com.oleksandr.entity.Task> getByEmployeeId(long employeeId);

    String complete(long taskId, long employeeId);

    String setDelay(long id, long d);
}
