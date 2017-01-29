package com.oleksandr.controller;

import com.oleksandr.controller.admin.EmployeeRole;
import com.oleksandr.entity.Employee;
import com.oleksandr.service.entity.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

/**
 * Created by Nuts on 1/14/2017
 * 2:19 PM.
 */
@Controller
@Scope("session")
@SessionAttributes(names = "employee", types = Employee.class)
public class LoginController {
    private final EmployeeService employeeService;

    @Autowired
    public LoginController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @RequestMapping(value = {"/loginForm", "/"}, method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout) {
        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid username and password!");
        }

        if (logout != null) {
            model.addObject("msg", "You've been logged out successfully.");
        }
        model.setViewName("redirect:/loginFormPage.html");

        return model;
    }

    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public ModelAndView accessDenied() {

        ModelAndView model = new ModelAndView();

        //check if user is login
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetail = (UserDetails) auth.getPrincipal();
            model.addObject("username", userDetail.getUsername());
        }

        model.setViewName("redirect:/403page.html");
        return model;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout() {

        ModelAndView model = new ModelAndView();

        //check if user is login
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetail = (UserDetails) auth.getPrincipal();
            model.addObject("username", userDetail.getUsername());
        }

        model.setViewName("redirect:/403page.html");
        return model;
    }

    @RequestMapping(value = "/routing", method = RequestMethod.GET)
    public ModelAndView routing(Principal principal) {
        ModelAndView model = new ModelAndView();
        long employeeId = Long.parseLong(principal.getName());

        Employee employee = employeeService.getById(employeeId);
        model.getModel().put("employee", employee);

        switch (EmployeeRole.valueOf(EmployeeRole.class, employee.getRole().getName().toUpperCase().trim())) {
            case ROLE_ADMIN: {
                model.setViewName("redirect:/admin/project.html");
                break;
            }
            case ROLE_CUSTOMER: {
                model.setViewName("redirect:/customer/projects.html");
                break;
            }
            case ROLE_EMPLOYEE: {
                model.setViewName("redirect:/employee/employee.html");
                break;
            }
            case ROLE_MANAGER: {
                model.setViewName("redirect:/manager/projects.html");
                break;
            }
            default: model.setViewName("/WEB-INF/views/403page.html");
        }

        return model;
    }
}
