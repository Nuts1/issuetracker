package com.oleksandr.service.entity.impl;

import com.oleksandr.dao.TaskEmployeeDao;
import com.oleksandr.entity.Employee;
import com.oleksandr.entity.TaskEmployee;
import com.oleksandr.service.entity.TaskEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Nuts on 1/13/2017
 * 12:56 PM.
 */
@Service
public class TaskEmployeeServiceImpl implements TaskEmployeeService {


    private final TaskEmployeeDao dao;

    @Autowired
    public TaskEmployeeServiceImpl(TaskEmployeeDao dao) {
        this.dao = dao;
    }

    @Override
    public List<TaskEmployee> getEmployeeTasksByEmployeeId(long employeeId) {
        return dao.getEmployeeTasksByEmployeeId(employeeId);
    }

    @Override
    public List<TaskEmployee> getEmployeeTasksByEmployee(Employee employee) {
        return dao.getEmployeeTasksByEmployee(employee);
    }

    @Override
    public TaskEmployee getByEmployeeAndTaskId(long taskId, long employeeId) {
        return dao.getByEmployeeAndTaskId(taskId, employeeId);
    }

    @Override
    public String confirm(long taskId, long employeeId) {
        if(dao.confirm(taskId, employeeId)) {
            return "Success";
        } else {
            return "Error";
        }
    }
}
