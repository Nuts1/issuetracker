package com.oleksandr.service.entity.impl;

import com.oleksandr.controller.admin.EmployeeRole;
import com.oleksandr.entity.Employee;
import com.oleksandr.service.entity.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
/**
 * Created by Nuts on 1/5/2017
 * 3:34 PM.
 **/

@Service("userService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final EmployeeService employeeService;

    @Autowired
    public UserDetailsServiceImpl(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee employee = employeeService.getEmployee(email);
        return new User(String.valueOf(employee.getEmployeeId()), employee.getPassword(), getAuthorities(employee.getRole().getName()));
    }

    private List<SimpleGrantedAuthority> getAuthorities(String role) {
        List<SimpleGrantedAuthority> authList = new ArrayList<>();
        /*
        if (role != null && role.trim().length() > 0) {
            if (role.equalsIgnoreCase(EmployeeRole.ADMIN.toString())) {
                authList.add(new SimpleGrantedAuthority(EmployeeRole.ADMIN.toString()));
            }
            if (role.equalsIgnoreCase(EmployeeRole.EMPLOYEE.toString())) {
                authList.add(new SimpleGrantedAuthority(EmployeeRole.EMPLOYEE.toString()));
            }
            if (role.equalsIgnoreCase("ROLE_CUSTOMER")) {
                authList.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
            }
            if (role.equalsIgnoreCase("ROLE_MANAGER")) {
                authList.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
            }
        }
        */
        try {
            authList.add(new SimpleGrantedAuthority(EmployeeRole.valueOf(role).name()));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return authList;
    }
}