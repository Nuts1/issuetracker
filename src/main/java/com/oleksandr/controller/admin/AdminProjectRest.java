package com.oleksandr.controller.admin;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.dto.ProjectDto;
import com.oleksandr.entity.Employee;
import com.oleksandr.entity.Project;
import com.oleksandr.entity.json.Views;
import com.oleksandr.service.entity.ProjectService;
import com.oleksandr.validator.ProjectValidator;
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
public class AdminProjectRest {
    private final ProjectService projectService;

    private final ProjectValidator projectValidator;

    private final MessageSource messageSource;

    @Autowired
    public AdminProjectRest(ProjectService projectService, ProjectValidator projectValidator, MessageSource messageSource) {
        this.projectService = projectService;
        this.projectValidator = projectValidator;
        this.messageSource = messageSource;
    }

    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/admin/addProject")
    public List<String> addProject(@RequestBody ProjectDto projectDto, BindingResult bindingResult) {
        projectValidator.validateSave(projectDto, bindingResult);
        if(!bindingResult.hasErrors()) {
            projectService.save(projectDto);
        }
        return bindingResult.getAllErrors()
                .stream()
                .map(e -> messageSource.getMessage(e, LocaleContextHolder.getLocale()))
                .collect(Collectors.toList());
    }


    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/admin/updateProject")
    public List<String> updateProject(@RequestBody ProjectDto projectDto, BindingResult bindingResult) {
        projectValidator.validateSave(projectDto, bindingResult);
        if(!bindingResult.hasErrors()) {
            projectService.update(projectDto);
        }
        return bindingResult.getAllErrors()
                .stream()
                .map(e -> messageSource.getMessage(e, LocaleContextHolder.getLocale()))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/admin/deleteProject")
    public void deleteProject(@RequestParam long projectId) {
        projectService.delete(projectId);
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
