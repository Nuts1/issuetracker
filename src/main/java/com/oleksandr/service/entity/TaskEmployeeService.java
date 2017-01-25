package com.oleksandr.service.entity;

import com.oleksandr.entity.Employee;
import com.oleksandr.entity.TaskEmployee;

import java.util.List;

/**
 * Created by Nuts on 1/10/2017
 * 9:59 PM.
 */
public interface TaskEmployeeService {
    List<TaskEmployee> getEmployeeTasksByEmployeeId(long employeeId);
    List<TaskEmployee> getEmployeeTasksByEmployee(Employee employee);

    TaskEmployee getByEmployeeAndTaskId(long taskId, long employeeId);

    String confirm(long id, long employeeId);
}
