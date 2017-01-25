package com.oleksandr.service.entity.impl;

import com.oleksandr.dao.RoleDao;
import com.oleksandr.entity.Role;
import com.oleksandr.service.entity.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Nuts on 1/14/2017
 * 12:33 AM.
 */
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleDao dao;

    @Autowired
    public RoleServiceImpl(RoleDao dao) {
        this.dao = dao;
    }

    @Override
    public Role getById(long id) {
        return dao.getById(id);
    }

    @Override
    public List<Role> getAll() {
        return dao.getAll();
    }
}
