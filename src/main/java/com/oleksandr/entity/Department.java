package com.oleksandr.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.json.Views;

/**
 * Created by Nuts on 1/6/2017
 * 6:35 PM.
 */
public class Department {
    @JsonView({Views.Department.class})
    private long departmentId;

    @JsonView({Views.Department.class})
    private String name;

    public long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(long departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
