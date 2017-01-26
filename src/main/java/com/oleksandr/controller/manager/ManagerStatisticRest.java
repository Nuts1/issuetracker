package com.oleksandr.controller.manager;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.Employee;
import com.oleksandr.entity.json.Views;
import com.oleksandr.service.reports.ResourceListServiceImpl;
import com.oleksandr.service.reports.data.resourselist.ResourceListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

/**
 * Created by nuts on 25.01.17.
 */
@RestController
@Scope("session")
@SessionAttributes(names = "employee", types = Employee.class)
public class ManagerStatisticRest {
    private final ResourceListServiceImpl resourceListService;

    @Autowired
    public ManagerStatisticRest(ResourceListServiceImpl resourceListService) {
        this.resourceListService = resourceListService;
    }

    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/manager/resources", method = RequestMethod.GET)
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
}
