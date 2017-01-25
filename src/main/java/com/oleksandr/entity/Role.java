package com.oleksandr.entity;


import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.json.Views;

public class Role {
    @JsonView(Views.Summary.class)
    private long roleId;

    @JsonView(Views.Summary.class)
    private String name;

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}