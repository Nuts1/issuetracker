package com.oleksandr.controller.admin;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.dto.EmployeeDto;
import com.oleksandr.entity.Employee;
import com.oleksandr.entity.Position;
import com.oleksandr.entity.Qualification;
import com.oleksandr.entity.Role;
import com.oleksandr.entity.json.Views;
import com.oleksandr.service.entity.*;
import com.oleksandr.service.util.ErrorUtil;
import com.oleksandr.validator.EmployeeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by nuts on 25.01.17.
 */
@RestController
@Scope("session")
@SessionAttributes(names = "employee", types = Employee.class)
public class AdminEmployeeRest {
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final PositionService positionService;
    private final QualificationService qualificationService;
    private final RoleService roleService;


    private final EmployeeValidator employeeValidator;
    private final ErrorUtil errorUtil;

    @Autowired
    public AdminEmployeeRest(PositionService positionService,
                             RoleService roleService,
                             QualificationService qualificationService,
                             DepartmentService departmentService,
                             EmployeeService employeeService,
                             EmployeeValidator employeeValidator,
                             ErrorUtil errorUtil) {
        this.departmentService = departmentService;
        this.qualificationService = qualificationService;
        this.employeeService = employeeService;
        this.roleService = roleService;
        this.positionService = positionService;
        this.employeeValidator = employeeValidator;
        this.errorUtil = errorUtil;
    }


    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/admin/employeeByRole")
    public List<Employee> getEmployeeByRole(@RequestParam("role") String role) {
        return employeeService.getByRoleId(Long.parseLong(role));
    }

    @RequestMapping(value = "/admin/addEmployee")
    public List<String> addEmployee(@RequestBody EmployeeDto employeeDto, BindingResult bindingResult) {
        employeeValidator.validateSave(employeeDto, bindingResult);
        if(!bindingResult.hasErrors()) {
            employeeService.save(employeeDto);
        }

        List<String> result = errorUtil.getErrors(bindingResult);

        if(result.size() == 0)
            result.add("Success added");

        return result;
    }

    @RequestMapping(value = "/admin/updateEmployee")
    public List<String> updateEmployee(@RequestBody EmployeeDto employeeDto, BindingResult bindingResult) {
        employeeValidator.validateUpdate(employeeDto, bindingResult);
        if(!bindingResult.hasErrors()) {
            employeeService.update(employeeDto);
        }
        List<String> result = errorUtil.getErrors(bindingResult);

        if(result.size() == 0)
            result.add("Success updated");

        return result;
    }

    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/admin/getRoles")
    public List<Role> getRoles() {
        return roleService.getAll();
    }

    @RequestMapping(value = "/admin/deleteEmployee")
    public String deleteEmployee(@RequestParam long employeeId) {
        int rows = employeeService.delete(employeeId);
        if (rows == 1) {
            return "Success deleted";
        } else {
            return "Error. Updated rows = " + rows;
        }
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
}
