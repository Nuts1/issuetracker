package com.oleksandr.controller.admin;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.dto.EmployeeDto;
import com.oleksandr.dto.ProjectDto;
import com.oleksandr.entity.*;
import com.oleksandr.entity.json.Views;
import com.oleksandr.service.entity.*;
import com.oleksandr.service.entity.impl.ProjectServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Nuts on 1/13/2017
 * 12:07 PM.
 */
@RestController
public class AdminRest {
    private final EmployeeService employeeService;
    private final ProjectServiceImpl projectService;
    private final DepartmentService departmentService;
    private final PositionService positionService;
    private final QualificationService qualificationService;
    private final RoleService roleService;

    @Autowired
    public AdminRest(ProjectServiceImpl projectService,
                     PositionService positionService,
                     RoleService roleService,
                     QualificationService qualificationService,
                     DepartmentService departmentService,
                     EmployeeService employeeService) {
        this.projectService = projectService;
        this.departmentService = departmentService;
        this.qualificationService = qualificationService;
        this.employeeService = employeeService;
        this.roleService = roleService;
        this.positionService = positionService;
    }

    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/admin/addProject")
    public int addProject(@RequestBody ProjectDto projectDto) {
        return projectService.save(projectDto);
    }


    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/admin/updateProject")
    public int updateProject(@RequestBody ProjectDto projectDto) {
        return projectService.update(projectDto);
    }

    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/admin/employeeByRole")
    public List<Employee> getEmployeeByRole(@RequestParam("role") String role) {
        return employeeService.getByRoleId(Long.parseLong(role));
    }

    @RequestMapping(value = "/admin/addEmployee")
    public int addEmployee(@RequestBody EmployeeDto employeeDto) {
        return employeeService.save(employeeDto);
    }

    @RequestMapping(value = "/admin/updateEmployee")
    public int updateEmployee(@RequestBody EmployeeDto employeeDto) {
        return employeeService.update(employeeDto);
    }

    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/admin/getRoles")
    public List<Role> getRoles() {
        return roleService.getAll();
    }

    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/admin/getQualification")
    public List<Qualification> getQualification() {
        return qualificationService.getAll();
    }


    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/admin/getPosition")
    public List<Position> getPosition() {
        return positionService.getAll();
    }


    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/admin/getEmployee")
    public Employee getEmployee(@RequestParam("id") String id) {
        return employeeService.getById(Long.parseLong(id));
    }

    @JsonView(Views.ProjectNameAndID.class)
    @RequestMapping(value = "/admin/projectList")
    public List<Project> getProjects() {
        return projectService.getAllNameAndId();
    }


    @JsonView(Views.ProjectForAdmin.class)
    @RequestMapping(value = "/admin/getProject")
    public Project getProject(@RequestParam("projectId") String idS) {
        try {
            long id = Long.parseLong(idS);
            return projectService.getById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
