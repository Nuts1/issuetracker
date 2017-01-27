package com.oleksandr.service.entity;

import com.oleksandr.dto.SprintDto;
import com.oleksandr.entity.Sprint;
import com.oleksandr.service.reports.data.project.Statistic;

import java.util.List;

/**
 * Created by Nuts on 1/5/2017
 * 6:08 PM.
 */
public interface SprintService {
    List<Sprint> getByProjectId(long id);
    Sprint getById(long id);
    SprintDto getDtoById(long id);

    List<Sprint> getAll();

    int save(SprintDto sprint);

    int update(SprintDto sprintDto);

    Statistic getStatistic(long idPr);

    Statistic getStatisticTask(long id);

    boolean delete(Long sprintId);
}
