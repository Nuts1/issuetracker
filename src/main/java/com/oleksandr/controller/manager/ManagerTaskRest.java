package com.oleksandr.controller.manager;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.dto.TaskDto;
import com.oleksandr.service.entity.*;
import com.oleksandr.service.reports.data.project.Statistic;
import com.oleksandr.service.reports.data.resourselist.ResourceListDto;
import com.oleksandr.dto.SprintDto;
import com.oleksandr.entity.*;
import com.oleksandr.entity.json.Views;
import com.oleksandr.service.entity.impl.ProjectServiceImpl;
import com.oleksandr.service.reports.ResourceListServiceImpl;
import com.oleksandr.service.entity.impl.SprintServiceImpl;
import com.oleksandr.service.util.ErrorUtil;
import com.oleksandr.validator.TaskValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by Nuts on 1/6/2017
 * 1:12 PM.
 */
@RestController
@Scope("session")
@SessionAttributes(names = "employee", types = Employee.class)
public class ManagerTaskRest {
    private final TaskService taskService;
    private final TaskValidator taskValidator;
    private final ErrorUtil errorUtil;


    @Autowired
    public ManagerTaskRest(ProjectServiceImpl projectService,
                           TaskService taskService,
                           TaskValidator taskValidator,
                           EmployeeService employeeService,
                           ErrorUtil errorUtil) {
        this.taskService = taskService;
        this.taskValidator = taskValidator;
        this.errorUtil = errorUtil;
    }

    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/manager/getTask")
    public com.oleksandr.entity.Task getTask(@RequestParam String idTask) {
        try {
            long id = Long.parseLong(idTask);
            return taskService.getById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @RequestMapping(value = "/manager/delete", method = RequestMethod.POST)
    public void delete(@RequestBody String taskId) {
        try {
            long id = Long.parseLong(taskId);
            taskService.delete(id);
        } catch (NumberFormatException ignore) {
        }
    }

    @RequestMapping(value = "/manager/save", method = RequestMethod.POST)
    public List<String> saveTask(@RequestBody TaskDto task, BindingResult bindingResult) {
        taskValidator.validateSave(task, bindingResult);
        if(!bindingResult.hasErrors()) {
            taskService.save(task);
        }

        List<String> result = errorUtil.getErrors(bindingResult);

        if(result.size() == 0)
            result.add("Success added");

        return result;
    }

    @RequestMapping(value = "/manager/edit", method = RequestMethod.POST)
    public List<String> editTask(@RequestBody TaskDto task, BindingResult bindingResult) {
        taskValidator.validateUpdate(task, bindingResult);
        if(!bindingResult.hasErrors()) {
            taskService.update(task);
        }

        List<String> result = errorUtil.getErrors(bindingResult);

        if(result.size() == 0)
            result.add("Success updated");

        return result;
    }

    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/manager/getTaskBySprintId")
    public List<Task> getTaskBySprintId(@RequestParam("idSprint") String idSprint) {
        try {
            long id = Long.parseLong(idSprint);
            return taskService.getBySprintId(id);
        } catch (NumberFormatException ignore) {
        }
        return null;
    }
}