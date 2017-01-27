package com.oleksandr.dao;

import com.oleksandr.dto.*;
import com.oleksandr.entity.Employee;

import java.util.List;

/**
 * Created by Nuts on 1/5/2017
 * 3:40 PM.
 */
public interface EmployeeDao {
    int save(EmployeeDto employeeDto);

    int update(EmployeeDto employeeDto);

    int delete(long id);

    Employee getById(long id);

    Employee getEmployeeByIds(Long[] id);

    Employee getEmployeeByEmail(String login);

    List<Employee> getEmployeeFromTask(long id);

    List<Employee> getByDeptIdAndPosId(Long idDep, Long idPo);

    List<Employee> getByProjectId(long idProject);

    List<Employee> getByRoleId(long role);

    boolean changePassword(String newPassHash, long id);

    List<Employee> getAll();

    List<Employee> getByProjectIdAndEmployeeId(long idProject, long idEmployee);
}
