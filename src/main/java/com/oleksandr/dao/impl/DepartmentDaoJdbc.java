package com.oleksandr.dao.impl;

import com.oleksandr.dao.DepartmentDao;
import com.oleksandr.entity.Department;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.oleksandr.dao.Util.close;

/**
 * Created by Nuts on 1/6/2017
 * 6:37 PM.
 */
@Repository
public class DepartmentDaoJdbc implements DepartmentDao {
    @Resource(name = "dataSource")
    private DataSource dataSource;

    private static final String SELECT_BY_ID = "SELECT d.id_department AS department_id, " +
            "d.name AS name " +
            "FROM department d " +
            "WHERE d.id_department = ?";

    private static final String SELECT_ALL = "SELECT d.id_department AS department_id, " +
            "d.name AS name " +
            "FROM department d ";

    @Override
    public Department getById(long id) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            Department department = mapDepartment(resultSet);
            close(statement);
            return department;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public List<Department> getAll() {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
            resultSet = statement.executeQuery();
            List<Department> result = mapDepartments(resultSet);
            close(statement);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    private List<Department> mapDepartments(ResultSet resultSet) {
        List<Department> departments = null;
        try {
            departments = new ArrayList<>();
            while(resultSet.next()) {
                departments.add(map(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    private Department mapDepartment(ResultSet resultSet) {
        try {
            if(resultSet.next()) {
                return map(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Department map(ResultSet resultSet) throws SQLException {
        Department department = new Department();
        department.setName(resultSet.getString("name"));
        department.setDepartmentId(resultSet.getLong("department_id"));

        return department;
    }

}
