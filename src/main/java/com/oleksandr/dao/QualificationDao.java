package com.oleksandr.dao;

import com.oleksandr.entity.Qualification;

import java.util.List;

/**
 * Created by Nuts on 1/5/2017
 * 6:30 PM.
 */
public interface QualificationDao {
    Qualification getById(long id);

    List<Qualification> getAll();
}
