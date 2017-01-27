package com.oleksandr.dao;

import com.oleksandr.dto.ProjectDto;
import com.oleksandr.entity.Employee;
import com.oleksandr.entity.Project;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Nuts on 1/5/2017
 * 2:49 PM.
 */
public interface ProjectDao {
    Project getById(long name);

    List<Project> getAllNameAndId();

    int delete(long id);

    ProjectDto getProjectDtoById(long idProject);

    int save(ProjectDto projectDto);

    int update(ProjectDto projectDto);

    List<Project> getAllNameAndIdByManagerId(long employeeId);

    List<ProjectDto> getProjectDtos(long employeeId);

    List<ProjectDto> getProjectDtos(long employeeId, long idProject);

    LinkedHashMap<String,String> getStatistic(long id);

    LinkedHashMap<String, String> getStatisticTask(long id);

    List<Project> getAllNameAndIdByCustomerId(long employeeId);
}
