package com.oleksandr.service.entity.impl;

import com.oleksandr.dao.PositionDao;
import com.oleksandr.entity.Position;
import com.oleksandr.service.entity.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Nuts on 1/13/2017
 * 12:55 PM.
 */
@Service
public class PositionServiceImpl implements PositionService {

    private final PositionDao dao;

    @Autowired
    public PositionServiceImpl(PositionDao dao) {
        this.dao = dao;
    }

    @Override
    public Position getById(long id) {
        return dao.getById(id);
    }

    @Override
    public List<Position> getAll() {
        return dao.getAll();
    }
}
