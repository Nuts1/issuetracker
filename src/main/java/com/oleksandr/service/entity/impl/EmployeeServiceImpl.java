package com.oleksandr.service.entity.impl;

/**
 * Created by Nuts on 1/5/2017
 * 3:39 PM.
 */

import com.oleksandr.dao.EmployeeDao;
import com.oleksandr.dto.EmployeeDto;
import com.oleksandr.entity.Employee;
import com.oleksandr.service.entity.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private static final int STRENGTH = 11;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(STRENGTH);

    private final EmployeeDao dao;

    @Autowired
    public EmployeeServiceImpl(EmployeeDao employeeDao) {
        this.dao = employeeDao;
    }

    @Override
    public Employee getEmployee(String email) {
        return dao.getEmployeeByEmail(email);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int save(EmployeeDto employeeDto) {
        employeeDto.setPassword(passwordEncoder.encode("1111"));
        return dao.save(employeeDto);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int update(EmployeeDto employeeDto) {
        return dao.update(employeeDto);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int delete(long id) {
        return dao.delete(id);
    }


    @Override
    public List<Employee> getByDeptIdAndPosId(long idDep, long idPo) {
        return dao.getByDeptIdAndPosId(idDep, idPo);
    }

    @Override
    public List<Employee> getByRoleId(long role) {
        return dao.getByRoleId(role);
    }

    @Override
    public Employee getById(long id) {
        return dao.getById(id);
    }

    @Override
    public boolean changePassword(String newPassHash, long id) {
        return dao.changePassword(newPassHash, id);
    }

    @Override
    public List<Employee> getByProjectId(long idProject) {
        return dao.getByProjectId(idProject);
    }

    @Override
    public List<Employee> getAll() {
        return dao.getAll();
    }

    @Override
    public Employee getEmployeeByEmail(String email) {
        return dao.getEmployeeByEmail(email);
    }
}