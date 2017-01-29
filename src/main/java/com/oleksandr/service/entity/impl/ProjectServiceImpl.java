package com.oleksandr.service.entity.impl;

import com.oleksandr.dao.ProjectDao;
import com.oleksandr.dto.ProjectDto;
import com.oleksandr.entity.Project;
import com.oleksandr.service.entity.ProjectService;
import com.oleksandr.service.reports.data.project.Statistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Nuts on 1/13/2017
 * 12:55 PM.
 */
@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectDao dao;

    @Autowired
    public ProjectServiceImpl(ProjectDao dao) {
        this.dao = dao;
    }

    @Override
    public Project getById(long id) {
        return dao.getById(id);
    }

    @Override
    public List<Project> getAllNameAndId() {
        return dao.getAllNameAndId();
    }

    @Override
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public int delete(long id) {
        return dao.delete(id);
    }

    @Override
    public ProjectDto getProjectDtoById(long idProject) {
        return dao.getProjectDtoById(idProject);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public int save(ProjectDto projectDto) {
        return dao.save(projectDto);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public int update(ProjectDto projectDto) {
        return dao.update(projectDto);
    }

    @Override
    public List<Project> getAllNameAndIdByManagerId(long employeeId) {
        return dao.getAllNameAndIdByManagerId(employeeId);
    }

    @Override
    public Statistic getStatistic(long id) {
        Statistic statistic = new Statistic();
        statistic.setStatistic(dao.getStatistic(id));
        return statistic;
    }

    @Override
    public Statistic getStatisticTask(long id) {
        Statistic statistic = new Statistic();
        statistic.setStatistic(dao.getStatisticTask(id));
        return statistic;
    }

    @Override
    public List<Project> getAllNameAndIdByCustomerId(long employeeId) {
        return dao.getAllNameAndIdByCustomerId(employeeId);
    }
}
