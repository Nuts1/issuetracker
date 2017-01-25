package com.oleksandr.dao.impl;

import com.oleksandr.dao.QualificationDao;
import com.oleksandr.entity.Qualification;
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
 * Created by Nuts on 1/5/2017
 * 6:30 PM.
 */
@Repository
public class QualificationDaoJdbc implements QualificationDao {
    @Resource(name = "dataSource")
    private DataSource dataSource;

    private static final String SELECT_BY_ID = "SELECT qualification_id AS qualification_id, " +
            "qualification AS qualification " +
            "FROM qualification " +
            "WHERE qualification_id = ?";

    private static final String SELECT_ALL = "SELECT qualification_id AS qualification_id, " +
            "qualification AS qualification " +
            "FROM qualification ";

    @Override
    public Qualification getById(long id) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            Qualification qualification = mapQualification(resultSet);
            close(statement);
            return qualification;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public List<Qualification> getAll() {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
            resultSet = statement.executeQuery();
            List<Qualification> qualifications = mapQualifications(resultSet);
            close(statement);
            return qualifications;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    private List<Qualification> mapQualifications(ResultSet resultSet) throws SQLException {
        List<Qualification> qualifications = new ArrayList<>();
        while(resultSet.next()) {
            try {
                qualifications.add(map(resultSet));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return qualifications;
    }

    private Qualification mapQualification(ResultSet resultSet) throws SQLException {
        if(resultSet.next()) {
            try {
                return map(resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Qualification map(ResultSet resultSet) throws SQLException {
        Qualification qualification = new Qualification();
        qualification.setQualification(resultSet.getString("qualification"));
        qualification.setQualificationId(resultSet.getLong("qualification_id"));
        return qualification;
    }

}
