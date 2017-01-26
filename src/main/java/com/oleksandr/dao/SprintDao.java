package com.oleksandr.dao;

import com.oleksandr.dao.impl.ProjectDaoJdbc;
import com.oleksandr.dto.SprintDto;
import com.oleksandr.entity.Sprint;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Nuts on 1/5/2017
 * 6:08 PM.
 */
public interface SprintDao {
    List<Sprint> getByProjectId(long id);
    Sprint getById(long id);
    SprintDto getDtoById(long id);

    List<Sprint> getAll();

    int save(SprintDto sprint);

    int update(SprintDto sprintDto);

    List<SprintDto> getSprints(long employeeId, long projectId);

    LinkedHashMap<String, String> getSprintStatistic(long id);

    int delete(Long sprintId);

    List<SprintDto> getAllDependentSprintDto(long id);
}
