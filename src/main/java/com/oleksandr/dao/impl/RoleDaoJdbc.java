package com.oleksandr.dao.impl;

import com.oleksandr.dao.RoleDao;
import com.oleksandr.entity.Role;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.oleksandr.dao.Util.*;

/**
 * Created by Nuts on 1/5/2017
 * 6:29 PM.
 */
@Repository
public class RoleDaoJdbc implements RoleDao {
    @Resource(name = "dataSource")
    private DataSource dataSource;


    private static final String SELECT_BY_ID = "SELECT role_id AS role_id, " +
            "name AS name " +
            "FROM role " +
            "WHERE role_id = ?";

    private static final String SELECT_ALL = "SELECT role_id AS role_id, " +
            "name AS name " +
            "FROM role ";

    @Override
    public Role getById(long id) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            Role role = mapRole(resultSet);
            close(statement);
            return role;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public List<Role> getAll() {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
            resultSet = statement.executeQuery();
            List<Role> roles = mapRoles(resultSet);
            close(statement);
            return roles;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    private List<Role> mapRoles(ResultSet resultSet) throws SQLException {
        List<Role> roles = new ArrayList<>();
        while (resultSet.next()) {
            roles.add(map(resultSet));
        }
        return roles;
    }

    private Role mapRole(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return map(resultSet);
        }
        return null;
    }

    private Role map(ResultSet resultSet) throws SQLException {
        Role role = new Role();
        role.setName(resultSet.getString("name"));
        role.setRoleId(resultSet.getLong("role_id"));
        return role;
    }
}