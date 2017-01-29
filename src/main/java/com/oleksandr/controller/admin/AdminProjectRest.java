package com.oleksandr.controller.admin;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.dto.ProjectDto;
import com.oleksandr.entity.Employee;
import com.oleksandr.entity.Project;
import com.oleksandr.entity.json.Views;
import com.oleksandr.service.entity.ProjectService;
import com.oleksandr.service.util.ErrorUtil;
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

    private final ErrorUtil errorUtil;

    @Autowired
    public AdminProjectRest(ProjectService projectService, ProjectValidator projectValidator, ErrorUtil errorUtil) {
        this.projectService = projectService;
        this.projectValidator = projectValidator;
        this.errorUtil = errorUtil;
    }

    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/admin/addProject")
    public List<String> addProject(@RequestBody ProjectDto projectDto, BindingResult bindingResult) {
        projectValidator.validateSave(projectDto, bindingResult);
        if(!bindingResult.hasErrors()) {
            projectService.save(projectDto);
        }
        List<String> result = errorUtil.getErrors(bindingResult);

        if(result.size() == 0)
            result.add("Success added");
        return result;
    }


    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/admin/updateProject")
    public List<String> updateProject(@RequestBody ProjectDto projectDto, BindingResult bindingResult) {
        projectValidator.validateSave(projectDto, bindingResult);
        if(!bindingResult.hasErrors()) {
            projectService.update(projectDto);
        }
        List<String> result = errorUtil.getErrors(bindingResult);

        if(result.size() == 0)
            result.add("Success updated");
        return result;
    }

    @RequestMapping(value = "/admin/deleteProject")
    public String deleteProject(@RequestParam long projectId) {
        int rows = projectService.delete(projectId);
        if (rows == 1) {
            return "Success deleted";
        } else {
            return "Error. Updated rows = " + rows;
        }
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
