package com.oleksandr.dao;

import com.oleksandr.entity.Employee;
import com.oleksandr.entity.TaskEmployee;

import java.util.List;

/**
 * Created by Nuts on 1/10/2017
 * 9:59 PM.
 */
public interface TaskEmployeeDao {
    List<TaskEmployee> getEmployeeTasksByEmployeeId(long employeeId);

    List<TaskEmployee> getEmployeeTasksByEmployee(Employee employee);

    TaskEmployee getByEmployeeAndTaskId(long taskId, long employeeId);

    List<TaskEmployee> getEmployeeTasksByEmployeeAndSprint(Employee employee, Long idProject);

    boolean confirm(long taskId, long employeeId);

    List<TaskEmployee> getEmployeeTasksByEmployeeAndProject(Employee employee, Long idProject);
}
