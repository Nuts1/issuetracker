package com.oleksandr.controller.manager;

import com.oleksandr.entity.Employee;
import com.oleksandr.service.entity.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Created by Nuts on 1/4/2017
 * 5:01 PM.
 */
@Controller
@Scope("session")
@SessionAttributes(names = "employee", types = Employee.class)
public class ManagerController {
    private final EmployeeService employeeService;

    @Autowired
    public ManagerController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @RequestMapping(value = "/manager")
    public String welcome(Principal principal, Model model) {
        long employeeId = Long.parseLong(principal.getName()); // getName return employeeId; TODO
        //long employeeId = 1;
        Employee employee = employeeService.getById(employeeId);
        model.addAttribute("employee", employee);
        return "redirect:/manager/projects.html";
    }

}
