package com.oleksandr.controller.employee;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.Employee;
import com.oleksandr.entity.Task;
import com.oleksandr.entity.TaskEmployee;
import com.oleksandr.entity.json.Views;
import com.oleksandr.service.entity.EmployeeService;
import com.oleksandr.service.entity.TaskEmployeeService;
import com.oleksandr.service.entity.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Nuts on 1/13/2017
 * 2:24 PM.
 */
@Scope("session")
@SessionAttributes(names = "employee", types = Employee.class)
@RestController
public class EmployeeRest {
    private final TaskService taskService;
    private final TaskEmployeeService taskEmployeeService;

    @Autowired
    public EmployeeRest(TaskService taskService,
                        TaskEmployeeService taskEmployeeService,
                        EmployeeService employeeService) {
        this.taskService = taskService;
        this.taskEmployeeService = taskEmployeeService;
    }

    @JsonView(Views.TaskEmployee.class)
    @RequestMapping(value = "/employee/getTasks")
    public List<Task> getTasks(@ModelAttribute("employee") Employee employee) {
        return taskService.getByEmployeeId(employee.getEmployeeId());
    }

    @JsonView(Views.TaskEmployee.class)
    @RequestMapping(value = "/employee/getTask")
    public TaskEmployee getTask(@RequestParam String taskId, @ModelAttribute("employee") Employee employee) {
        long id = Long.parseLong(taskId);
        return taskEmployeeService.getByEmployeeAndTaskId(id, employee.getEmployeeId());
    }

    @JsonView(Views.TaskEmployee.class)
    @RequestMapping(value = "/employee/confirmTask")
    public String confirmTask(@RequestParam String taskId, @ModelAttribute("employee") Employee employee) {
        long id = Long.parseLong(taskId);
        return taskEmployeeService.confirm(id, employee.getEmployeeId());
    }

    @JsonView(Views.TaskEmployee.class)
    @RequestMapping(value = "/employee/completeTask")
    public String completeTask(@RequestParam String taskId, @ModelAttribute("employee") Employee employee) {
        long id = Long.parseLong(taskId);
        return taskService.complete(id, employee.getEmployeeId());
    }


    @JsonView(Views.TaskEmployee.class)
    @RequestMapping(value = "/employee/setDelay")
    public String setDelay(@RequestParam String taskId, @RequestParam String delay) {
        long id = Long.parseLong(taskId);
        long d = Long.parseLong(delay);
        return taskService.setDelay(id, d);
    }
}
