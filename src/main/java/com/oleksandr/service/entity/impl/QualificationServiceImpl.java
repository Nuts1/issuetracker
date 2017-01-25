package com.oleksandr.service.entity.impl;

import com.oleksandr.dao.QualificationDao;
import com.oleksandr.entity.Qualification;
import com.oleksandr.service.entity.QualificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Nuts on 1/14/2017
 * 1:07 AM.
 */
@Service
public class QualificationServiceImpl implements QualificationService {
    private final QualificationDao dao;

    @Autowired
    public QualificationServiceImpl(QualificationDao dao) {
        this.dao = dao;
    }

    @Override
    public Qualification getById(long id) {
        return dao.getById(id);
    }

    @Override
    public List<Qualification> getAll() {
        return dao.getAll();
    }
}
