package com.oleksandr.dao.impl;

import com.oleksandr.dao.PositionDao;
import com.oleksandr.entity.Position;
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
 * 6:45 PM.
 */
@Repository
public class PositionDaoImpl implements PositionDao {

    @Resource(name="dataSource")
    private DataSource dataSource;

    private static final String SELECT_BY_ID = "SELECT position as position, " +
            "position_id as position_id " +
            "FROM position " +
            "WHERE position_id = ?";

    private static final String SELECT_ALL = "SELECT position as position, " +
            "position_id as position_id " +
            "FROM position ";

    @Override
    public Position getById(long id) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            Position position = mapPosition(resultSet);
            close(statement);
            return position;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public List<Position> getAll() {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
            resultSet = statement.executeQuery();
            List<Position> positions = mapPositions(resultSet);
            close(statement);
            return positions;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    private List<Position> mapPositions(ResultSet resultSet) throws SQLException {
        List<Position> positions = new ArrayList<>();
        while(resultSet.next()) {
            try {
                positions.add(map(resultSet));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return positions;
    }

    private Position mapPosition(ResultSet resultSet) throws SQLException {
        if(resultSet.next()) {
            Position position = new Position();
            try {
                return map(resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Position map(ResultSet resultSet) throws SQLException {
        Position position = new Position();
        position.setPosition(resultSet.getString("position"));
        position.setPositionId(resultSet.getLong("position_id"));
        return position;
    }
}
