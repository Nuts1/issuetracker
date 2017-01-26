package com.oleksandr.service.entity.impl;

import com.oleksandr.dao.SprintDao;
import com.oleksandr.dto.SprintDto;
import com.oleksandr.entity.Sprint;
import com.oleksandr.service.entity.SprintService;
import com.oleksandr.service.reports.data.project.Statistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Nuts on 1/13/2017
 * 12:56 PM.
 */
@Service
public class SprintServiceImpl implements SprintService {
    private final SprintDao dao;

    @Autowired
    public SprintServiceImpl(SprintDao dao) {
        this.dao = dao;
    }

    @Override
    public List<Sprint> getByProjectId(long id) {
        return dao.getByProjectId(id);
    }

    @Override
    public Sprint getById(long id) {
        return dao.getById(id);
    }

    @Override
    public SprintDto getDtoById(long id) {
        return dao.getDtoById(id);
    }

    @Override
    public List<Sprint> getAll() {
        return dao.getAll();
    }

    @Override
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public int save(SprintDto sprint) {
        return dao.save(sprint);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public int update(SprintDto sprintDto) {
        return dao.update(sprintDto);
    }

    @Override
    public Statistic getStatistic(long id) {
        Statistic statistic = new Statistic();
        statistic.setStatistic(dao.getSprintStatistic(id));
        return statistic;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public boolean delete(Long sprintId) {
        List<SprintDto> sprints = dao.getAllDependentSprintDto(sprintId); // dependentSprint.previous_sprint_id = sprintId;
        if(sprints != null) {
            SprintDto sprintDto = dao.getDtoById(sprintId);
            sprints.forEach(e -> {
                e.setPreviousSprint(sprintDto.getPreviousSprint());
                dao.update(e);
            });
        }
        return dao.delete(sprintId) > 0;
    }
}
