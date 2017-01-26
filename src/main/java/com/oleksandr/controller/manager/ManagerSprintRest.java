package com.oleksandr.controller.manager;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.dto.SprintDto;
import com.oleksandr.entity.Employee;
import com.oleksandr.entity.Sprint;
import com.oleksandr.entity.json.Views;
import com.oleksandr.service.entity.impl.SprintServiceImpl;
import com.oleksandr.service.reports.data.project.Statistic;
import com.oleksandr.validator.SprintValidator;
import com.oleksandr.validator.TaskValidator;
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
public class ManagerSprintRest {
    private final SprintServiceImpl sprintService;
    private final SprintValidator sprintValidator;
    private final MessageSource messageSource;

    @Autowired
    public ManagerSprintRest(SprintServiceImpl sprintService,
                             SprintValidator sprintValidator,
                             MessageSource messageSource) {
        this.sprintService = sprintService;
        this.sprintValidator = sprintValidator;
        this.messageSource = messageSource;
    }


    @RequestMapping(value = "/manager/saveSprint", method = RequestMethod.POST)
    public List<String> saveSprint(@RequestBody SprintDto sprint, BindingResult bindingResult) {
        sprintValidator.validateSave(sprint, bindingResult);
        if(!bindingResult.hasErrors()) {
            sprintService.save(sprint);
        }

        return bindingResult.getAllErrors()
                .stream()
                .map(e -> messageSource.getMessage(e, LocaleContextHolder.getLocale()))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/manager/editSprint", method = RequestMethod.POST)
    public List<String> editSprint(@RequestBody SprintDto sprint, BindingResult bindingResult) {
        sprintValidator.validateUpdate(sprint, bindingResult);
        if(!bindingResult.hasErrors()) {
            sprintService.update(sprint);
        }

        return bindingResult.getAllErrors()
                .stream()
                .map(e -> messageSource.getMessage(e, LocaleContextHolder.getLocale()))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/manager/deleteSprint", method = RequestMethod.POST)
    public String deleteSprint(@RequestBody String sprintId) {
        try {
            long id = Long.parseLong(sprintId);
            if(sprintService.delete(id)) {
                return "Success";
            }
        } catch (NumberFormatException ignore) {
            return "Id is invalid";
        }
        return "Error";
    }

    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/manager/sprintStatistic")
    public Statistic getSprintStatistic(@RequestParam String idSprint) {
        try {
            long idSpr = Long.parseLong(idSprint);
            return sprintService.getStatistic(idSpr);
        } catch (NumberFormatException ignore) {
            return null;
        }
    }

    @JsonView(Views.SprintDateIdPrevIdAndName.class)
    @RequestMapping(value = "/manager/sprintInfo")
    public Sprint getSprint(@RequestParam("idSprint") String idSprint) {
        try {
            long id = Long.parseLong(idSprint);
            return sprintService.getById(id);
        } catch (NumberFormatException ignore) {
        }
        return null;
    }

    @JsonView(Views.SprintDateIdPrevIdAndName.class)
    @RequestMapping(value = "/manager/getSprint")
    public SprintDto getSprintData(@RequestBody String idSprint) {
        try {
            long id = Long.parseLong(idSprint);
            return sprintService.getDtoById(id);
        } catch (NumberFormatException ignore) {
        }
        return null;
    }

    @JsonView(Views.SprintDateIdPrevIdAndName.class)
    @RequestMapping(value = "/manager/sprints")
    public List<Sprint> getSprints() {
        return sprintService.getAll();
    }

    @JsonView(Views.SprintDateIdPrevIdAndName.class)
    @RequestMapping(value = "/manager/sprintsByProjectId")
    public List<Sprint> getSprintsByProjectId(@RequestParam String projectId) {
        try {
            long id = Long.parseLong(projectId);
            return sprintService.getByProjectId(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
