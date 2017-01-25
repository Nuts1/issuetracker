package com.oleksandr.service.reports.data.resourselist;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.json.Views;

import java.util.Date;


import java.util.List;

/**
 * Created by Nuts on 1/10/2017
 * 6:46 PM.
 */
public class ResourceListDto {
    @JsonView(Views.Summary.class)
    Date[] interval = new Date[2];

    // interval[0] - start interval date
    // interval[1] - completion interval date

    @JsonView(Views.Summary.class)
    private List<EmployeeListItem> employees;

    public ResourceListDto(List<EmployeeListItem> employees) {
        this.employees = employees;
        final Date[] interval = {null, null};
        employees.forEach(employee -> {
            employee.getTotalDayWorks().forEach(dayWork -> {
                if(interval[0] == null || interval[0].compareTo(dayWork.date) > 0) {
                    interval[0] = dayWork.date;
                }
                if(interval[1] == null || interval[1].compareTo(dayWork.date) < 0) {
                    interval[1] = dayWork.date;
                }
            });
        });
        this.interval = interval;
    }

    public List<EmployeeListItem> getEmployees() {
        return employees;
    }
}

