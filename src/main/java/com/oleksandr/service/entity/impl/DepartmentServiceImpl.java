package com.oleksandr.service.entity.impl;

import com.oleksandr.dao.DepartmentDao;
import com.oleksandr.entity.Department;
import com.oleksandr.service.entity.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Nuts on 1/13/2017
 * 12:53 PM.
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentDao dao;

    @Autowired
    public DepartmentServiceImpl(DepartmentDao departmentDao) {
        this.dao = departmentDao;
    }

    @Override
    public Department getById(long id) {
        return dao.getById(id);
    }

    @Override
    public List<Department> getAll() {
        return dao.getAll();
    }
}