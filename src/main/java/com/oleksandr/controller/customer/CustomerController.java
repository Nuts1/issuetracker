package com.oleksandr.controller.customer;

/**
 * Created by nuts on 26.01.17.
 */

import com.oleksandr.entity.Employee;
import com.oleksandr.service.entity.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.security.Principal;

@Controller
@Scope("session")
@SessionAttributes(names = "employee", types = Employee.class)
public class CustomerController {
    private final EmployeeService employeeService;

    @Autowired
    public CustomerController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @RequestMapping(value = "/customer")
    public String welcome(Principal principal, Model model) {
        long employeeId = Long.parseLong(principal.getName()); // getName return employeeId; TODO
        //long employeeId = 4;
        Employee employee = employeeService.getById(employeeId);
        model.addAttribute("employee", employee);
        return "redirect:/customer/projects.html";
    }

}
