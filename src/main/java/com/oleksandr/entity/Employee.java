package com.oleksandr.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.json.Views;

public class Employee {
    @JsonView({Views.Summary.class, Views.EmployeeIdNameSurname.class, Views.ProjectForAdmin.class})
    private long employeeId;

    @JsonView({Views.Summary.class, Views.EmployeeIdNameSurname.class, Views.ProjectForAdmin.class})
    private String name;

    @JsonView({Views.Summary.class, Views.EmployeeIdNameSurname.class, Views.ProjectForAdmin.class})
    private String surname;

    @JsonView({Views.Summary.class})
    private String email;

    @JsonView(Views.Summary.class)
    private Role role;

    private String password;

    @JsonView(Views.Summary.class)
    private Position position;

    @JsonView(Views.Summary.class)
    private Qualification qualification;

    @JsonView(Views.Summary.class)
    private Department department;

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Qualification getQualification() {
        return qualification;
    }

    public void setQualification(Qualification qualification) {
        this.qualification = qualification;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", role=" + role +
                ", password='" + password + '\'' +
                ", position=" + position +
                ", qualification=" + qualification +
                ", department=" + department +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
