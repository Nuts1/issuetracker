package com.oleksandr.dao.impl;

import com.oleksandr.dao.SprintDao;
import com.oleksandr.dao.TaskDao;
import com.oleksandr.dto.SprintDto;
import com.oleksandr.entity.Sprint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import static com.oleksandr.dao.Util.*;

/**
 * Created by Nuts on 1/5/2017
 * 6:09 PM.
 */
@Repository
public class SprintDaoJdbc implements SprintDao {
    @Resource(name = "dataSource")
    private DataSource dataSource;

    private final TaskDao taskDao;

    private static final String SELECT_BY_PROJECT_ID = "SELECT sprint_id AS sprint_id, " +
            "name AS name, " +
            "start_date AS start_date," +
            "completion_date AS completion_date, " +
            "project_id AS project_id, " +
            "previous_sprint_id AS previous_sprint_id," +
            "sprint_id AS sprint_id " +
            "FROM sprint " +
            "WHERE project_id = ?" +
            "ORDER BY start_date";

    private static final String SELECT_BY_PREVIOUS_SPRINT_ID = "SELECT sprint_id AS sprint_id, " +
            "name AS name, " +
            "start_date AS start_date," +
            "completion_date AS completion_date, " +
            "project_id AS project_id, " +
            "previous_sprint_id AS previous_sprint_id," +
            "sprint_id AS sprint_id " +
            "FROM sprint " +
            "WHERE previous_sprint_id = ?" +
            "ORDER BY start_date";

    private static final String SELECT_SPRINT_STATISTIC = "SELECT 'Delay (based on the last completed task.\n" +
            "In working hours\n" +
            "(8 hours per day))', " +
            "  ((EXTRACT(EPOCH FROM(t.actual_completion_date - t.completion_date))/86400)::FLOAT * 24 " +
            "  - (EXTRACT(EPOCH FROM(t.actual_completion_date - t.completion_date)/86400)::INTEGER*16)) " +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "WHERE sprint_id = ?  AND\n" +
            "  t.actual_completion_date = (SELECT MAX(actual_completion_date)\n" +
            "                                FROM project INNER JOIN sprint s USING(project_id)\n" +
            "                                INNER JOIN task t USING(sprint_id)\n" +
            "                               WHERE sprint_id = ?)\n" +
            "UNION ALL\n" +
            "SELECT 'Count tasks with delay', COUNT(task_id) " +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "WHERE sprint_id = ?  AND\n" +
            "  t.actual_completion_date <= t.completion_date\n" +
            "GROUP BY sprint_id\n" +
            "UNION ALL\n" +
            "SELECT 'Count tasks finished in time', COUNT(task_id) " +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "WHERE sprint_id = ?  AND\n" +
            "  t.actual_completion_date >= t.completion_date\n" +
            "GROUP BY sprint_id\n" +
            "UNION ALL\n" +
            "SELECT 'Man hours (based on estimate)', SUM((te.load / 100.0) * t.estimate) " +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "INNER JOIN task_employee te USING(task_id)\n" +
            "WHERE sprint_id = ?\n" +
            "GROUP BY sprint_id\n" +
            "UNION ALL\n" +
            "SELECT 'Man hours (Forecast)', SUM((te.load / 100.0) * \n" +
            "  ((EXTRACT(EPOCH FROM (coalesce(t.actual_completion_date, t.completion_date) - coalesce(t.actual_start_date, t.start_date)))/86400)::FLOAT*24 " +
            "  - (EXTRACT(EPOCH FROM(coalesce(t.actual_completion_date, t.completion_date) - coalesce(t.actual_start_date, t.start_date))/86400)::INTEGER*16))) " +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "INNER JOIN task_employee te USING(task_id)\n" +
            "WHERE sprint_id = ?\n" +
            "GROUP BY sprint_id\n" +
            "UNION ALL\n" +
            "SELECT 'Man hours (Actual)', SUM((te.load / 100.0) * \n" +
            "  ((EXTRACT(EPOCH FROM(actual_completion_date - coalesce(t.actual_start_date, t.start_date)))/86400)::FLOAT * 24 \n" +
            "  - (EXTRACT(EPOCH FROM(t.actual_completion_date - coalesce(t.actual_start_date, t.start_date))/86400)::INTEGER*16)))\n" +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "INNER JOIN task_employee te USING(task_id)\n" +
            "WHERE sprint_id = ? AND t.actual_completion_date IS NOT NULL\n" +
            "GROUP BY sprint_id\n";

    private static final String PROJECT_TASK_STATISTIC = "SELECT 'Count tasks with delay', COUNT(task_id) " +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "WHERE sprint_id = ?  AND\n" +
            "  t.actual_completion_date > t.completion_date\n" +
            "GROUP BY sprint_id\n" +
            "UNION ALL\n" +
            "SELECT 'Count tasks finished in time', COUNT(task_id) " +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "WHERE sprint_id = ?  AND\n" +
            "  t.actual_completion_date <= t.completion_date\n" +
            "GROUP BY sprint_id\n" +
            "UNION ALL\n" +
            "SELECT 'Count tasks', COUNT(task_id) " +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "WHERE sprint_id = ? \n" +
            "GROUP BY sprint_id\n";


    private static final String SELECT_TASK_WITH_DELAY = "SELECT 'Task: ' || t.name, 'Delay: ' || (te.load / 100.0) * \n" +
            "((EXTRACT(EPOCH FROM(actual_completion_date - t.completion_date))/86400)::FLOAT * 24 \n" +
            "- (EXTRACT(EPOCH FROM(actual_completion_date - t.completion_date)/86400)::INTEGER*16))\n" +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "INNER JOIN task_employee te USING(task_id)\n" +
            "WHERE sprint_id = ? AND \n" +
            "t.actual_completion_date IS NOT NULL AND\n" +
            "t.actual_completion_date > t.completion_date;";


    private static final String SELECT_BY_EMPLOYEE_AND_PROJECT = "SELECT DISTINCT sprint_id AS sprint_id, " +
            "sprint.name AS name, " +
            "sprint.start_date AS start_date," +
            "sprint.completion_date AS completion_date, " +
            "project_id AS project_id, " +
            "previous_sprint_id AS previous_sprint_id," +
            "sprint_id AS sprint_id " +
            "FROM sprint INNER JOIN task USING(sprint_id)" +
            "INNER JOIN task_employee USING(task_id)" +
            "WHERE employee_id = ? AND project_id = ?" +
            "ORDER BY start_date";

    private static final String SELECT_BY_ID = "SELECT sprint_id AS sprint_id, " +
            "name AS name, " +
            "start_date AS start_date," +
            "completion_date AS completion_date, " +
            "project_id AS project_id, " +
            "previous_sprint_id AS previous_sprint_id," +
            "sprint_id AS sprint_id " +
            "FROM sprint " +
            "WHERE sprint_id = ?";

    private static final String SELECT_ALL = "SELECT sprint_id AS sprint_id, " +
            "name AS name, " +
            "start_date AS start_date," +
            "completion_date AS completion_date, " +
            "project_id AS project_id, " +
            "previous_sprint_id AS previous_sprint_id," +
            "sprint_id AS sprint_id " +
            "FROM sprint ";


    private static final String UPDATE_SPRINT = "UPDATE sprint " +
            "SET name = ?," +
            "start_date = ?," +
            "completion_date = ?," +
            "previous_sprint_id = ?," +
            "project_id = ? " +
            "WHERE sprint_id = ?";


    private static final String SAVE_SPRINT = "INSERT INTO sprint " +
            "(sprint_id, name, start_date, completion_date, project_id, previous_sprint_id)" +
            " VALUES ( nextval('seq') , ?, ?, ?, ?, ?)";

    private static final String DELETE_BY_ID = "DELETE FROM sprint " +
            "WHERE sprint_id=?";


    @Autowired
    public SprintDaoJdbc(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public LinkedHashMap<String, String> getStatisticTask(long id) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();

            PreparedStatement statement = connection.prepareStatement(PROJECT_TASK_STATISTIC);
            statement.setLong(1, id);
            statement.setLong(2, id);
            statement.setLong(3, id);

            resultSet = statement.executeQuery();
            return ProjectDaoJdbc.mapStatistic(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public List<Sprint> getByProjectId(long id) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_PROJECT_ID);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            List<Sprint> sprints = mapSprints(resultSet);
            close(statement);
            return sprints;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public Sprint getById(long id) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            Sprint sprint = mapSprint(resultSet);
            close(statement);
            return sprint;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public SprintDto getDtoById(long id) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            SprintDto sprintDto = mapSprintDto(resultSet);
            close(statement);
            return sprintDto;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    private SprintDto mapSprintDto(ResultSet resultSet) throws SQLException {
        SprintDto sprintDto = null;
        if (resultSet.next()) {
            sprintDto = new SprintDto();
            try {
                sprintDto.setSprintId(resultSet.getLong("sprint_id"));
                sprintDto.setName(resultSet.getString("name"));
                sprintDto.setCompletionDate(resultSet.getDate("completion_date"));
                sprintDto.setPreviousSprint(resultSet.getString("previous_sprint_id"));
                sprintDto.setSprintId(resultSet.getLong("sprint_id"));
                sprintDto.setStartDate(resultSet.getDate("start_date"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return sprintDto;

    }

    @Override
    public List<Sprint> getAll() {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
            resultSet = statement.executeQuery();
            List<Sprint> sprint = mapSprints(resultSet);
            close(statement);
            return sprint;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }


    @Override
    public int save(SprintDto sprint) {
        Connection connection = null;
        try {
            int row;
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SAVE_SPRINT);
            statement.setString(1, sprint.getName());
            statement.setTimestamp(2, new Timestamp(sprint.getStartDate().getTime()));
            statement.setTimestamp(3,  new Timestamp(sprint.getCompletionDate().getTime()));
            statement.setLong(4, sprint.getProjectId());
            if(sprint.getPreviousSprint() != null && sprint.getPreviousSprint().length() > 0) {
                statement.setLong(5, Long.parseLong(sprint.getPreviousSprint()));
            } else {
                statement.setNull(5, Types.BIGINT);
            }
            row =+ statement.executeUpdate();
            connection.commit();
            close(statement);
            return row;
        } catch (SQLException e) {
            rollBack(connection, e);
        } finally {
            close(connection);
        }
        return 0;
    }

    @Override
    public int update(SprintDto sprintDto) {
        Connection connection = null;
        int rows = 0;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(UPDATE_SPRINT);
            statement.setString(1, sprintDto.getName());
            statement.setTimestamp(2, new Timestamp(sprintDto.getStartDate().getTime()));
            statement.setTimestamp(3, new Timestamp(sprintDto.getCompletionDate().getTime()));
            if(sprintDto.getPreviousSprint() != null && sprintDto.getPreviousSprint().length() > 0) {
                statement.setLong(4, Long.parseLong(sprintDto.getPreviousSprint()));
            } else {
                statement.setNull(4, Types.BIGINT);
            }
            statement.setLong(5, sprintDto.getProjectId());
            statement.setLong(6, sprintDto.getSprintId());
            rows = +statement.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            rollBack(connection, e);
            e.printStackTrace();
        } finally {
            close(connection);
        }
        return rows;
    }

    @Override
    public List<SprintDto> getSprints(long employeeId, long projectId) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_EMPLOYEE_AND_PROJECT);
            statement.setLong(1, employeeId);
            statement.setLong(2, projectId);
            resultSet = statement.executeQuery();
            List<SprintDto> sprint = mapSprintsDto(resultSet);
            close(statement);
            return sprint;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }

    }

    @Override
    public LinkedHashMap<String, String> getSprintStatistic(long id) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_SPRINT_STATISTIC);
            statement.setLong(1, id);
            statement.setLong(2, id);
            statement.setLong(3, id);
            statement.setLong(4, id);
            statement.setLong(5, id);
            statement.setLong(6, id);
            statement.setLong(7, id);
            resultSet = statement.executeQuery();
            LinkedHashMap<String, String> result = ProjectDaoJdbc.mapStatistic(resultSet);
            close(statement);

            statement = connection.prepareStatement(SELECT_TASK_WITH_DELAY);
            statement.setLong(1, id);
            result.putAll(ProjectDaoJdbc.mapStatistic(statement.executeQuery()));

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public int delete(Long sprintId) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID);
            statement.setLong(1, sprintId);
            int row = statement.executeUpdate();
            close(statement);
            connection.commit();
            return row;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection);
        }
        return 0;
    }

    @Override
    public List<SprintDto> getAllDependentSprintDto(long id) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_PREVIOUS_SPRINT_ID);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            List<SprintDto> sprints = mapSprintsDto(resultSet);
            close(statement);
            return sprints;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    private List<SprintDto> mapSprintsDto(ResultSet resultSet) throws SQLException {
        List<SprintDto> sprints = new ArrayList<>();
        while (resultSet.next()) {
            try {
                sprints.add(mapDto(resultSet));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return sprints;
    }

    private SprintDto mapDto(ResultSet resultSet) throws SQLException {
        SprintDto sprint = new SprintDto();

        sprint.setSprintId(resultSet.getLong("sprint_id"));
        sprint.setName(resultSet.getString("name"));
        sprint.setCompletionDate(resultSet.getDate("completion_date"));
        sprint.setPreviousSprint(resultSet.getString("previous_sprint_id"));
        sprint.setSprintId(resultSet.getLong("sprint_id"));
        sprint.setStartDate(resultSet.getDate("start_date"));
        return sprint;
    }

    private Sprint mapSprint(ResultSet resultSet) throws SQLException {
        Sprint sprint = null;
        if (resultSet.next()) {
            sprint = new Sprint();
            try {
                map(resultSet, sprint);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return sprint;
    }

    private List<Sprint> mapSprints(ResultSet resultSet) throws SQLException {
        List<Sprint> sprints = new ArrayList<>();
        while (resultSet.next()) {
            Sprint sprint = new Sprint();
            try {
                map(resultSet, sprint);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            sprints.add(sprint);
        }
        return sprints;
    }

    private void map(ResultSet resultSet, Sprint sprint) throws SQLException {
        sprint.setSprintId(resultSet.getLong("sprint_id"));
        sprint.setName(resultSet.getString("name"));
        sprint.setCompletionDate(resultSet.getDate("completion_date"));
        sprint.setPreviousSprint(resultSet
                .getObject("previous_sprint_id") != null ? resultSet.getLong("previous_sprint_id") : null);
        sprint.setSprintId(resultSet.getLong("sprint_id"));
        sprint.setStartDate(resultSet.getDate("start_date"));
        sprint.setTasks(taskDao.getBySprintId(resultSet.getLong("sprint_id")));
    }
}
