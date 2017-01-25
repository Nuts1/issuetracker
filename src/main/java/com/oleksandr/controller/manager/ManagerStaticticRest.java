package com.oleksandr.controller.manager;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.json.Views;
import com.oleksandr.service.reports.ResourceListServiceImpl;
import com.oleksandr.service.reports.data.resourselist.ResourceListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by nuts on 25.01.17.
 */
@RestController
public class ManagerStaticticRest {
    private final ResourceListServiceImpl resourceListService;

    @Autowired
    public ManagerStaticticRest(ResourceListServiceImpl resourceListService) {
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
