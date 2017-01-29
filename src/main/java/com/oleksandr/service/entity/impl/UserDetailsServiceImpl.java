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


        if (role != null && role.trim().length() > 0) {
            role = role.trim();
            System.out.printf(role);
            if (role.equalsIgnoreCase(EmployeeRole.ROLE_ADMIN.toString())) {
                authList.add(new SimpleGrantedAuthority(EmployeeRole.ROLE_ADMIN.toString()));
                System.out.println("A");
            }
            if (role.equalsIgnoreCase(EmployeeRole.ROLE_EMPLOYEE.toString())) {
                authList.add(new SimpleGrantedAuthority(EmployeeRole.ROLE_EMPLOYEE.toString()));
                System.out.println("A");
            }
            if (role.equalsIgnoreCase(EmployeeRole.ROLE_MANAGER.toString())) {
                authList.add(new SimpleGrantedAuthority(EmployeeRole.ROLE_MANAGER.toString()));
                System.out.println("A");
            }
            if (role.equalsIgnoreCase(EmployeeRole.ROLE_CUSTOMER.toString())) {
                authList.add(new SimpleGrantedAuthority(EmployeeRole.ROLE_CUSTOMER.toString()));
                System.out.println("A");
            }
        }
        return authList;
    }
}

