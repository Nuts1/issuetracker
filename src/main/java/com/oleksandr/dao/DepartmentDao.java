package com.oleksandr.dao;

import com.oleksandr.entity.Department;

import java.util.List;

/**
 * Created by Nuts on 1/6/2017
 * 6:37 PM.
 */
public interface DepartmentDao {
    Department getById(long id);
    List<Department> getAll();
}
