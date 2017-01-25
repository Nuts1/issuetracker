package com.oleksandr.controller.manager;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.dao.DepartmentDao;
import com.oleksandr.dao.EmployeeDao;
import com.oleksandr.dao.PositionDao;
import com.oleksandr.dao.impl.EmployeeDaoJdbc;
import com.oleksandr.entity.Department;
import com.oleksandr.entity.Employee;
import com.oleksandr.entity.Position;
import com.oleksandr.entity.json.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by nuts on 25.01.17.
 */
@RestController
public class ManagerEmployeeRest {
    private final EmployeeDao employeeService;
    private final DepartmentDao departmentService;
    private final PositionDao positionService;

    @Autowired
    public ManagerEmployeeRest(EmployeeDao employeeService, DepartmentDao departmentService, PositionDao positionService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.positionService = positionService;
    }

    @JsonView(Views.EmployeeIdNameSurname.class)
    @RequestMapping(value = "/manager/employees")
    public List<Employee> getEmployees(@RequestParam String idProject) {
        try {
            long idPr = Long.parseLong(idProject);
            return employeeService.getByProjectId(idPr);
        } catch (NumberFormatException ignore) {
            return employeeService.getAll();
        }
    }

    @JsonView(Views.EmployeeIdNameSurname.class)
    @RequestMapping(value = "/manager/applyFilter")
    public List<Employee> applyFilter(@RequestParam("idDept") String idDept,
                                      @RequestParam("idPos") String idPos) {
        try {
            long idDep = Long.parseLong(idDept);
            long idPo = Long.parseLong(idPos);
            return employeeService.getByDeptIdAndPosId(idDep, idPo);
        } catch (NumberFormatException ignore) {
        }
        return null;
    }

    @JsonView(Views.Department.class)
    @RequestMapping(value = "/manager/departments")
    public List<Department> getDepartments() {
        return departmentService.getAll();
    }

    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/manager/positions")
    public List<Position> getPositions() {
        return positionService.getAll();
    }

}
