package com.oleksandr.dao.impl;

import com.oleksandr.dao.EmployeeDao;
import com.oleksandr.dao.TaskDao;
import com.oleksandr.dto.TaskDto;
import com.oleksandr.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.Date;

import static com.oleksandr.dao.Util.*;

/**
 * Created by Nuts on 1/5/2017
 * 8:59 PM.
 */
@Repository
public class TaskDaoJdbc implements TaskDao {

    @Resource(name = "dataSource")
    private DataSource dataSource;

    private final EmployeeDao employeeDao;

    private static final String SELECT_BY_SPRINT_ID = "SELECT DISTINCT task_id AS task_id, " +
            "name AS name, " +
            "start_date AS start_date, " +
            "estimate AS estimate, " +
            "sprint_id AS sprint_id, " +
            "subtask_id AS subtask_id, " +
            "previous_task_id AS previous_task_id, " +
            "description AS description, " +
            "completion_date AS completion_date, " +
            "predicted_delay AS predicted_delay, " +
            "actual_completion_date AS actual_completion_date, " +
            "actual_start_date AS actual_start_date " +
            "FROM task " +
            "WHERE sprint_id = ?";

    private static final String SELECT_BY_EMPLOYEE_ID = "SELECT task_id AS task_id, " +
            "t.name AS name, " +
            "t.start_date AS start_date, " +
            "t.estimate AS estimate, " +
            "t.sprint_id AS sprint_id, " +
            "t.subtask_id AS subtask_id, " +
            "t.previous_task_id AS previous_task_id, " +
            "t.description AS description, " +
            "t.completion_date AS completion_date, " +
            "t.predicted_delay AS predicted_delay, " +
            "actual_completion_date AS actual_completion_date, " +
            "actual_start_date AS actual_start_date " +
            "FROM task t INNER JOIN task_employee te USING(task_id) " +
            "WHERE te.employee_id = ?";

    private static final String SELECT_BY_ID = "SELECT task_id AS task_id, " +
            "name AS name, " +
            "start_date AS start_date, " +
            "estimate AS estimate, " +
            "sprint_id AS sprint_id, " +
            "subtask_id AS subtask_id, " +
            "previous_task_id AS previous_task_id, " +
            "description AS description, " +
            "completion_date AS completion_date, " +
            "predicted_delay AS predicted_delay, " +
            "actual_completion_date AS actual_completion_date, " +
            "actual_start_date AS actual_start_date " +
            "FROM task " +
            "WHERE task_id = ?";

    private static final String SAVE_TASK = "INSERT INTO task " +
            "(task_id, name, start_date, estimate, sprint_id, previous_task_id, description, completion_date, predicted_delay)" +
            " VALUES ( nextval('seq') , ?, ?, ?, ?, ?, ?, ?, ?)  RETURNING task_id";

    private static final String SAVE_TASK_EMPLOYEE = "INSERT INTO task_employee" +
            "(id_task_employee, task_id, employee_id, confirm, load)" +
            "VALUES (nextval('seq'), ?, ?, ?, ?) ";

    private static final String DELETE_BY_ID = "DELETE FROM task " +
            "WHERE task_id=?";

    private static final String DELETE_TASK_EMPLOYEE_BY_TASK_ID = "DELETE FROM task_employee " +
            "WHERE task_id=?";

    private static final String UPDATE_TASK = "UPDATE task " +
            "SET name = ?," +
            "start_date = ?," +
            "estimate = ?," +
            "previous_task_id = ?," +
            "description = ?," +
            "completion_date = ?," +
            "predicted_delay = ? " +
            "WHERE task_id = ?";

    private static final String SET_ACTUAL_COMPLETE_DATE = "UPDATE task " +
            "SET actual_completion_date = ? " +
            "WHERE task_id = ?";

    private static final String SET_DELAY = "UPDATE task " +
            "SET predicted_delay = ? " +
            "WHERE task_id = ?";


    @Autowired
    public TaskDaoJdbc(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    @Override
    public com.oleksandr.entity.Task getById(long id) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            com.oleksandr.entity.Task task = mapTask(resultSet);
            close(statement);
            return task;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public com.oleksandr.entity.Task getByIdWithoutEmployees(long id) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            if(resultSet.next()) {
                com.oleksandr.entity.Task task = mapTaskWithoutEmployees(resultSet);
                close(statement);
                return task;
            } else {
                close(statement);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public boolean save(TaskDto task) {
        Connection connection = null;
        try {
            boolean result;
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(SAVE_TASK);
            setParamToAddTask(task, statement);
            result = statement.execute();
            ResultSet taskIdResultSet = statement.getResultSet();
            Long taskId = null;
            if(taskIdResultSet.next()) {
                taskId = taskIdResultSet.getLong(1);
            }
            close(statement);
            Long[] id = task.getIds();
            Integer[] units = task.getUnits();
            if(id != null) {
                for (int i = 0; i < id.length; i++) {
                    saveTaskEmployee(connection, taskId, id[i], units[i]);
                }
            }
            connection.commit();
            return result;
        } catch (SQLException e) {
            rollBack(connection, e);
        } finally {
            close(connection);
        }
        return false;
    }

    private void saveTaskEmployee(Connection connection, Long taskId, Long x, Integer load) throws SQLException {
        //"(task_id, employee_id, confirm)" +
        //"VALUES (?, ?, ?) ";
        PreparedStatement preparedStatement = connection.prepareStatement(SAVE_TASK_EMPLOYEE);
        preparedStatement.setLong(1, taskId);
        preparedStatement.setLong(2, x);
        preparedStatement.setLong(3, 0);
        preparedStatement.setLong(4, load);
        preparedStatement.executeUpdate();
        close(preparedStatement);
    }

    @Override
    public void delete(long id) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID);
            statement.setLong(1, id);
            statement.executeUpdate();
            close(statement);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection);
        }
    }

    @Override
    public int update(TaskDto task) {
        Connection connection = null;
        int rows = 0;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(UPDATE_TASK);
            statement.setString(1, task.getName());
            statement.setTimestamp(2, new Timestamp( task.getStartDate().getTime()));
            statement.setLong(3, task.getEstimate());
            if(task.getPreviousTaskId() != null && task.getPreviousTaskId().length() > 0) {
                statement.setLong(4, Long.parseLong(task.getPreviousTaskId()));
            } else {
                statement.setNull(4, Types.BIGINT);
            }
            statement.setString(5, task.getDescription());
            statement.setTimestamp(6, new Timestamp(task.getCompletionDate().getTime()));
            statement.setLong(7, task.getPredictedDelay());
            statement.setLong(8, task.getIdTask());
            rows =+ statement.executeUpdate();

            PreparedStatement preparedStatement = connection
                    .prepareStatement(DELETE_TASK_EMPLOYEE_BY_TASK_ID);
            preparedStatement.setLong(1, task.getIdTask());
            preparedStatement.executeUpdate();
            close(statement);

            Long[] id = task.getIds();
            Integer[] units = task.getUnits();
            if(id != null) {
                for (int i = 0; i < id.length; i++) {
                    saveTaskEmployee(connection, task.getIdTask(), id[i], units[i]);
                }
            }
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
    public List<com.oleksandr.entity.Task> getBySprintId(long id) {
        return  getByLongParam(id, SELECT_BY_SPRINT_ID);
    }

    @Override
    public List<com.oleksandr.entity.Task> getByEmployeeId(long employeeId) {
        return getByLongParam(employeeId, SELECT_BY_EMPLOYEE_ID);
    }

    private List<com.oleksandr.entity.Task> getByLongParam(long id, String sql) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            List<com.oleksandr.entity.Task> tasks = mapTasks(resultSet);
            close(statement);
            return tasks;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public void setActualCompleteDate(long taskId, Date time) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(SET_ACTUAL_COMPLETE_DATE);
            statement.setLong(2, taskId);
            statement.setTimestamp(1, new Timestamp( time.getTime()));
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            rollBack(connection, e);
            e.printStackTrace();
        } finally {
            close(connection);
        }
    }

    @Override
    public boolean setDelay(long id, long d) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(SET_DELAY);
            statement.setLong(2, id);
            statement.setLong(1, d);
            statement.executeUpdate();
            connection.commit();
            return true;
        } catch (SQLException e) {
            rollBack(connection, e);
            e.printStackTrace();
            return false;
        } finally {
            close(connection);
        }
    }


    private void setParamToAddTask(TaskDto task, PreparedStatement statement) throws SQLException {
        //"(name, start_date, estimate, sprint_id, previous_task_id, description, completion_date, predicted_delay)" +
        //" VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        statement.setString(1, task.getName());
        statement.setTimestamp(2, new Timestamp(task.getStartDate().getTime()));
        statement.setLong(3, task.getEstimate());
        statement.setLong(4, task.getSprintId());
        if(task.getPreviousTaskId() != null && task.getPreviousTaskId().length() > 0) {
            statement.setLong(5, Long.parseLong(task.getPreviousTaskId()));
        } else {
            statement.setNull(5, Types.BIGINT);
        }
        statement.setString(6, task.getDescription());
        statement.setTimestamp(7, new Timestamp(task.getCompletionDate().getTime()));
        statement.setLong(8, task.getPredictedDelay());
    }


    private List<Task> mapTasks(ResultSet resultSet) throws SQLException {
        List<com.oleksandr.entity.Task> tasks = new ArrayList<>();
        while (resultSet.next()) {
            tasks.add(map(resultSet));
        }
        return tasks;
    }

    private Task mapTask(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return map(resultSet);
        }
        return null;
    }

    private Task map(ResultSet resultSet) throws SQLException {
        com.oleksandr.entity.Task task = new com.oleksandr.entity.Task();
        Timestamp timestamp;
        task.setName(resultSet.getString("name"));
        task.setStartDate(new java.util.Date(resultSet.getTimestamp("start_date").getTime()));
        task.setCompletionDate(new java.util.Date(resultSet.getTimestamp("completion_date").getTime()));
        task.setDescription(resultSet.getString("description"));
        task.setEmployees(employeeDao.getEmployeeFromTask(resultSet.getLong("task_id")));
        task.setEstimate(resultSet.getInt("estimate"));
        task.setPredictedDelay(resultSet.getString("predicted_delay"));
        task.setPreviousTask(getById(resultSet.getLong("previous_task_id")));
        task.setSubtask(resultSet.getLong("subtask_id"));
        task.setTaskId(resultSet.getLong("task_id"));
        timestamp = resultSet.getTimestamp("actual_completion_date");
        if(timestamp != null)
            task.setActualCompletionDate(new java.util.Date(timestamp.getTime()));
        timestamp = resultSet.getTimestamp("actual_start_date");
        if(timestamp != null)
            task.setActualStartDate(new java.util.Date(timestamp.getTime()));
        return task;
    }

    private Task mapTaskWithoutEmployees(ResultSet resultSet) throws SQLException {
        com.oleksandr.entity.Task task = new com.oleksandr.entity.Task();
        Timestamp timestamp;
        task.setName(resultSet.getString("name"));
        task.setStartDate(new java.util.Date(resultSet.getTimestamp("start_date").getTime()));
        task.setCompletionDate(new java.util.Date(resultSet.getTimestamp("completion_date").getTime()));
        task.setDescription(resultSet.getString("description"));
        task.setEstimate(resultSet.getInt("estimate"));
        task.setPredictedDelay(resultSet.getString("predicted_delay"));
        task.setPreviousTask(getById(resultSet.getLong("previous_task_id")));
        task.setSubtask(resultSet.getLong("subtask_id"));
        task.setTaskId(resultSet.getLong("task_id"));
        timestamp = resultSet.getTimestamp("actual_completion_date");
        if(timestamp != null)
            task.setActualCompletionDate(new java.util.Date(timestamp.getTime()));
        timestamp = resultSet.getTimestamp("actual_start_date");
        if(timestamp != null)
            task.setActualStartDate(new java.util.Date(timestamp.getTime()));
        return task;
    }
}