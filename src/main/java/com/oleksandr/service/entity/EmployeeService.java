package com.oleksandr.service.entity;

import com.oleksandr.dto.EmployeeDto;
import com.oleksandr.entity.Employee;
import com.oleksandr.service.reports.data.project.Statistic;

import java.util.List;

/**
 * Created by Nuts on 1/5/2017
 * 3:35 PM.
 */

public interface EmployeeService {
    Employee getEmployee(String login);

    int save(EmployeeDto employeeDto);
    int update(EmployeeDto employeeDto);

    int delete(long id);

    List<Employee> getByDeptIdAndPosId(long idDep, long idPo);
    List<Employee> getByRoleId(long role);

    Employee getById(long l);

    boolean changePassword(String newPassHash, long id);

    List<Employee> getByProjectId(long idProject);
    List<Employee> getAll();
}
