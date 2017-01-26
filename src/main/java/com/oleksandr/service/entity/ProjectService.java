package com.oleksandr.service.entity;

import com.oleksandr.dto.ProjectDto;
import com.oleksandr.entity.Project;
import com.oleksandr.service.reports.data.project.Statistic;

import java.util.List;

/**
 * Created by Nuts on 1/5/2017
 * 2:49 PM.
 */
public interface ProjectService {
    Project getById(long id);

    List<Project> getAllNameAndId();

    void delete(long id);

    ProjectDto getProjectDtoById(long idProject);

    int save(ProjectDto projectDto);

    int update(ProjectDto projectDto);

    List<Project> getAllNameAndIdByManagerId(long employeeId);

    Statistic getStatistic(long id);

    List<Project> getAllNameAndIdByCustomerId(long employeeId);
}
