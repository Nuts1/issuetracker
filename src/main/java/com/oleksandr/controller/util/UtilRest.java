package com.oleksandr.controller.util;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.Employee;
import com.oleksandr.entity.json.Views;
import com.oleksandr.service.entity.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Nuts on 1/15/2017
 * 11:15 AM.
 */
@Scope("session")
@SessionAttributes(names = "employee", types = Employee.class)
@RestController
public class UtilRest {
    private final EmployeeService employeeService;

    private static final int STRENGTH = 11;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(STRENGTH);

    @Autowired
    public UtilRest(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/util/getEmployee")
    public Employee getEmployee(@ModelAttribute("employee") Employee employee) {
        return employee;
    }


    @RequestMapping(value = "/util/changePassword")
    public String changePassword(@ModelAttribute("employee") Employee employee,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword) {
        if(BCrypt.checkpw(oldPassword, employee.getPassword())) {
            String newPassHash = passwordEncoder.encode(newPassword);
            if(employeeService.changePassword(newPassHash, employee.getEmployeeId())) {
                employee.setPassword(newPassHash);
                return "Changed";
            } else {
                return "Server error";
            }
        } else {
            return "Incorrect old password";
        }
    }
}

