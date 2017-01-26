package com.oleksandr.controller.customer;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.dao.ProjectDao;
import com.oleksandr.dto.SprintDto;
import com.oleksandr.entity.Employee;
import com.oleksandr.entity.Project;
import com.oleksandr.entity.Sprint;
import com.oleksandr.entity.json.Views;
import com.oleksandr.service.entity.ProjectService;
import com.oleksandr.service.entity.impl.SprintServiceImpl;
import com.oleksandr.service.reports.ResourceListServiceImpl;
import com.oleksandr.service.reports.data.project.Statistic;
import com.oleksandr.service.reports.data.resourselist.ResourceListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by nuts on 26.01.17.
 */

@RestController
@Scope("session")
@SessionAttributes(names = "employee", types = Employee.class)
public class CustomerRest {

    private final ProjectService projectService;
    private final SprintServiceImpl sprintService;
    private final ResourceListServiceImpl resourceListService;


    @Autowired
    public CustomerRest(ProjectService projectService,
                        SprintServiceImpl sprintService,
                        ResourceListServiceImpl resourceListService) {
        this.projectService = projectService;
        this.sprintService = sprintService;
        this.resourceListService = resourceListService;
    }

    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/customer/projectStatistic")
    public Statistic getProjectStatistic(@RequestParam String idProject) {
        try {
            long idPr = Long.parseLong(idProject);
            Statistic statistic = projectService.getStatistic(idPr);
            return statistic;
        } catch (NumberFormatException ignore) {
            return null;
        }
    }


    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/customer/project")
    public Project getProject(@RequestParam("projectId") String idS, @ModelAttribute("employee") Employee employee) {
        try {
            long id = Long.parseLong(idS);
            Project project = projectService.getById(id);
            if(project.getCustomer().getEmployeeId() == employee.getEmployeeId()) {
                return project;
            } else {
                throw new AccessDeniedException("You do not have access to this page");
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @JsonView(Views.ProjectNameAndID.class)
    @RequestMapping(value = "/customer/projectList")
    public List<Project> getProjects(@ModelAttribute("employee") Employee employee) {
        return projectService.getAllNameAndIdByCustomerId(employee.getEmployeeId());
    }

    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/customer/resources", method = RequestMethod.GET)
    public ResourceListDto recourseList(@RequestParam String idProject, @RequestParam String idEmployee) {
        Long idPr = null;
        Long idEmp = null;
        try {
            idPr = Long.parseLong(idProject);
        } catch (NumberFormatException ignore) {
        }
        try {
            idEmp = Long.parseLong(idEmployee);
        } catch (NumberFormatException ignore) {
        }
        return resourceListService.getResourceList(idPr, idEmp);
    }


    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/customer/sprintStatistic")
    public Statistic getSprintStatistic(@RequestParam String idSprint) {
        try {
            long idSpr = Long.parseLong(idSprint);
            return sprintService.getStatistic(idSpr);
        } catch (NumberFormatException ignore) {
            return null;
        }
    }

    @JsonView(Views.SprintDateIdPrevIdAndName.class)
    @RequestMapping(value = "/customer/sprintInfo")
    public Sprint getSprint(@RequestParam("idSprint") String idSprint) {
        try {
            long id = Long.parseLong(idSprint);
            return sprintService.getById(id);
        } catch (NumberFormatException ignore) {
        }
        return null;
    }

    @JsonView(Views.SprintDateIdPrevIdAndName.class)
    @RequestMapping(value = "/customer/getSprint")
    public SprintDto getSprintData(@RequestBody String idSprint) {
        try {
            long id = Long.parseLong(idSprint);
            return sprintService.getDtoById(id);
        } catch (NumberFormatException ignore) {
        }
        return null;
    }

    @JsonView(Views.SprintDateIdPrevIdAndName.class)
    @RequestMapping(value = "/customer/sprints")
    public List<Sprint> getSprints() {
        return sprintService.getAll();
    }

    @JsonView(Views.SprintDateIdPrevIdAndName.class)
    @RequestMapping(value = "/customer/sprintsByProjectId")
    public List<Sprint> getSprintsByProjectId(@RequestParam String projectId) {
        try {
            long id = Long.parseLong(projectId);
            return sprintService.getByProjectId(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
