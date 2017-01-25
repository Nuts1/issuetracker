package com.oleksandr.service.entity;

import com.oleksandr.entity.Position;

import java.util.List;

/**
 * Created by Nuts on 1/5/2017
 * 6:44 PM.
 */
public interface PositionService {
    Position getById(long id);

    List<Position> getAll();
}
