package com.oleksandr.service.entity;

import com.oleksandr.entity.Role;

import java.util.List;

/**
 * Created by Nuts on 1/5/2017
 * 6:29 PM.
 */
public interface RoleService {
    Role getById(long id);

    List<Role> getAll();
}
