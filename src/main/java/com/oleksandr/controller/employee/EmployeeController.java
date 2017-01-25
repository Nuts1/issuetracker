package com.oleksandr.controller.employee;

import com.oleksandr.entity.Employee;
import com.oleksandr.service.entity.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.security.Principal;

/**
 * Created by Nuts on 1/13/2017
 * 2:24 PM.
 */
@Controller
@Scope("session")
@SessionAttributes(names = "employee", types = Employee.class)
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @RequestMapping(value = "/employee")
    public String welcome(Principal principal, Model model) {
        //long employeeId = Long.parseLong(principal.getName()); // getName return employeeId; TODO
        long employeeId = 2;
        Employee employee = employeeService.getById(employeeId);
        model.addAttribute("employee", employee);
        return "redirect:/employee/employee.html";
    }
}
