package com.oleksandr.dao.impl;

import com.oleksandr.dao.EmployeeDao;
import com.oleksandr.dao.ProjectDao;
import com.oleksandr.dao.SprintDao;
import com.oleksandr.dto.ProjectDto;
import com.oleksandr.entity.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.oleksandr.dao.Util.close;

/**
 * Created by Nuts on 1/5/2017
 * 2:50 PM.
 */
@Repository
public class ProjectDaoJdbc implements ProjectDao {
    @Resource(name = "dataSource")
    private DataSource dataSource;

    private final EmployeeDao employeeDao;

    private final SprintDao sprintDao;

    private static final String SELECT_BY_ID = "SELECT p.PROJECT_ID AS project_id, " +
            "p.name AS name, " +
            "p.CUSTOMER_ID AS customer, " +
            "p.MANAGER_ID AS manager, " +
            "p.START_DATE AS start_date, " +
            "p.COMPLETION_DATE AS completion_date," +
            "p.predicated_completion_date AS predicated_completion_date " +
            "FROM project p " +
            "WHERE p.project_id = ?";

    private static final String SELECT_BY_EMPLOYEE_ID = "SELECT DISTINCT PROJECT_ID AS project_id, " +
            "p.name AS name, " +
            "p.CUSTOMER_ID AS customer, " +
            "p.MANAGER_ID AS manager, " +
            "p.START_DATE AS start_date, " +
            "p.COMPLETION_DATE AS completion_date," +
            "p.predicated_completion_date AS predicated_completion_date " +
            "FROM project p INNER JOIN SPRINT s USING(project_id) " +
            "INNER JOIN task t USING(sprint_id) " +
            "INNER JOIN task_employee te USING(task_id) " +
            "WHERE employee_id = ?";

    private static final String SELECT_BY_EMPLOYEE_ID_AND_PROJECT_ID = "SELECT DISTINCT PROJECT_ID AS project_id, " +
            "p.name AS name, " +
            "p.CUSTOMER_ID AS customer, " +
            "p.MANAGER_ID AS manager, " +
            "p.START_DATE AS start_date, " +
            "p.COMPLETION_DATE AS completion_date," +
            "p.predicated_completion_date AS predicated_completion_date " +
            "FROM project p INNER JOIN SPRINT s USING(project_id) " +
            "INNER JOIN task t USING(sprint_id) " +
            "INNER JOIN task_employee te USING(task_id) " +
            "WHERE employee_id = ? AND project_id = ?";

    private static final String SELECT_PROJECT_DTO_BY_ID = "SELECT p.PROJECT_ID AS project_id, " +
            "p.name AS name, " +
            "p.START_DATE AS start_date, " +
            "p.COMPLETION_DATE AS completion_date " +
            "FROM project p " +
            "WHERE p.project_id = ?";

    private static final String SAVE = "INSERT INTO project " +
            "(project_id, name, start_date , completion_date, manager_id, customer_id)" +
            " VALUES ( nextval('seq') , ?, ?, ?, ?, ?)";

    private static final String UPDATE = "UPDATE project " +
            "SET name = ?," +
            "start_date = ?," +
            "completion_date = ?, " +
            "manager_id = ?, " +
            "customer_id = ? " +
            "WHERE project_id = ?";

    private static final String SELECT_STATISTIC = "SELECT 'Delay (based on the last completed task.\n" +
            "In working hours\n" +
            "(8 hours per day))', " +
            "  ((EXTRACT(EPOCH FROM(t.actual_completion_date - t.completion_date))/86400)::FLOAT * 24 " +
            "  - (EXTRACT(EPOCH FROM(t.actual_completion_date - t.completion_date)/86400)::INTEGER * 16)) " +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "WHERE project_id = ?  AND\n" +
            "  t.actual_completion_date = (SELECT MAX(actual_completion_date)\n" +
            "                                FROM project INNER JOIN sprint s USING(project_id)\n" +
            "                                INNER JOIN task t USING(sprint_id)\n" +
            "                               WHERE project_id = ?)\n" +
            "UNION ALL\n" +
            "SELECT 'Count tasks with delay', COUNT(task_id) " +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "WHERE project_id = ?  AND\n" +
            "  t.actual_completion_date > t.completion_date\n" +
            "GROUP BY project_id\n" +
            "UNION ALL\n" +
            "SELECT 'Count tasks finished in time', COUNT(task_id) " +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "WHERE project_id = ?  AND\n" +
            "  t.actual_completion_date <= t.completion_date\n" +
            "GROUP BY project_id\n" +
            "UNION ALL\n" +
            "SELECT 'Man hours (based on estimate)', SUM((te.load / 100.0) * t.estimate) " +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "INNER JOIN task_employee te USING(task_id)\n" +
            "WHERE project_id = ?\n" +
            "GROUP BY project_id\n" +
            "UNION ALL\n" +
            "SELECT 'Man hours (Forecast)', SUM((te.load / 100.0) * \n" +
            "  ((EXTRACT(EPOCH FROM (coalesce(t.actual_completion_date, t.completion_date) - coalesce(t.actual_start_date, t.start_date)))/86400)::FLOAT * 24 " +
            "  - (EXTRACT(EPOCH FROM(coalesce(t.actual_completion_date, t.completion_date) - coalesce(t.actual_start_date, t.start_date))/86400)::INTEGER * 16))) " +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "INNER JOIN task_employee te USING(task_id)\n" +
            "WHERE project_id = ?\n" +
            "GROUP BY project_id\n" +
            "UNION ALL\n" +
            "SELECT 'Man hours (Actual)', SUM((te.load / 100.0) * \n" +
            "  ((EXTRACT(EPOCH FROM(actual_completion_date - coalesce(t.actual_start_date, t.start_date)))/86400)::FLOAT * 24 \n" +
            "  - (EXTRACT(EPOCH FROM(t.actual_completion_date - coalesce(t.actual_start_date, t.start_date))/86400)::INTEGER*16)))\n" +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "INNER JOIN task_employee te USING(task_id)\n" +
            "WHERE project_id = ? AND t.actual_completion_date IS NOT NULL\n" +
            "GROUP BY project_id\n";

    private static final String SELECT_TASK_WITH_DELAY = "SELECT 'Task: ' || t.name, 'Delay: ' || (te.load / 100.0) * \n" +
            "((EXTRACT(EPOCH FROM(actual_completion_date - t.completion_date))/86400)::FLOAT * 24 \n" +
            "- (EXTRACT(EPOCH FROM(actual_completion_date - t.completion_date)/86400)::INTEGER*16))\n" +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "INNER JOIN task_employee te USING(task_id)\n" +
            "WHERE project_id = ? AND \n" +
            "t.actual_completion_date IS NOT NULL AND\n" +
            "t.actual_completion_date > t.completion_date;";

    private static final String SELECT_ALL = "SELECT p.name AS name," +
            "p.project_id AS project_id " +
            "FROM project p ";

    private static final String SELECT_BY_MANAGER_ID = "SELECT p.name AS name," +
            "p.project_id AS project_id " +
            "FROM project p " +
            "WHERE manager_id = ?";

    private static final String SELECT_BY_CUSTOMER_ID = "SELECT p.name AS name," +
            "p.project_id AS project_id " +
            "FROM project p " +
            "WHERE customer_id = ?";

    private static final String DELETE_BY_ID = "DELETE FROM project WHERE project_id = ?";

    private static final String PROJECT_TASK_STATISTIC = "SELECT 'Count tasks with delay', COUNT(task_id) " +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "WHERE project_id = ?  AND\n" +
            "  t.actual_completion_date > t.completion_date\n" +
            "GROUP BY project_id\n" +
            "UNION ALL\n" +
            "SELECT 'Count tasks finished in time', COUNT(task_id) " +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "WHERE project_id = ?  AND\n" +
            "  t.actual_completion_date <= t.completion_date\n" +
            "GROUP BY project_id\n" +
            "UNION ALL\n" +
            "SELECT 'Count tasks', COUNT(task_id) " +
            "FROM project INNER JOIN sprint s USING(project_id)\n" +
            "INNER JOIN task t USING(sprint_id)\n" +
            "WHERE project_id = ? \n" +
            "GROUP BY project_id\n";


    @Autowired
    public ProjectDaoJdbc(EmployeeDao employeeDao, SprintDao sprintDao) {
        this.employeeDao = employeeDao;
        this.sprintDao = sprintDao;
    }

    @Override
    public ProjectDto getProjectDtoById(long idProject) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_PROJECT_DTO_BY_ID);
            statement.setLong(1, idProject);
            resultSet = statement.executeQuery();
            ProjectDto result = mapProjectDto(resultSet);
            close(statement);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public int save(ProjectDto projectDto) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(SAVE);
            statement.setString(1, projectDto.getName());
            statement.setTimestamp(2, new Timestamp(projectDto.getStartDate().getTime()));
            statement.setTimestamp(3, new Timestamp(projectDto.getCompletionDate().getTime()));
            statement.setLong(4, Long.parseLong(projectDto.getManagerId()));
            statement.setLong(5, Long.parseLong(projectDto.getCustomerId()));
            int row = statement.executeUpdate();
            close(statement);
            connection.commit();
            return row;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            close(connection);
        }
    }

    @Override
    public int update(ProjectDto projectDto) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(UPDATE);
            statement.setString(1, projectDto.getName());
            statement.setTimestamp(2, new Timestamp(projectDto.getStartDate().getTime()));
            statement.setTimestamp(3, new Timestamp(projectDto.getCompletionDate().getTime()));
            statement.setLong(4, Long.parseLong(projectDto.getManagerId()));
            statement.setLong(5, Long.parseLong(projectDto.getCustomerId()));
            statement.setLong(6, Long.parseLong(projectDto.getProjectId()));
            int row = statement.executeUpdate();
            connection.commit();
            close(statement);
            return row;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            close(connection);
        }
    }

    @Override
    public List<Project> getAllNameAndIdByManagerId(long employeeId) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_MANAGER_ID);
            statement.setLong(1, employeeId);
            resultSet = statement.executeQuery();
            List<Project> result = mapProjectNameAndId(resultSet);
            close(statement);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public List<ProjectDto> getProjectDtos(long employeeId) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_EMPLOYEE_ID);
            statement.setLong(1, employeeId);
            resultSet = statement.executeQuery();
            List<ProjectDto> result = mapDtos(resultSet);
            close(statement);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
             close(connection, resultSet);
        }
    }

    @Override
    public List<ProjectDto> getProjectDtos(long employeeId, long projectId) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_EMPLOYEE_ID_AND_PROJECT_ID);
            statement.setLong(1, employeeId);
            statement.setLong(2, projectId);
            resultSet = statement.executeQuery();
            List<ProjectDto> result = mapDtos(resultSet);
            close(statement);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public LinkedHashMap<String, String> getStatistic(long id) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_STATISTIC);
            statement.setLong(1, id);
            statement.setLong(2, id);
            statement.setLong(3, id);
            statement.setLong(4, id);
            statement.setLong(5, id);
            statement.setLong(6, id);
            statement.setLong(7, id);
            resultSet = statement.executeQuery();
            LinkedHashMap<String, String> result = mapStatistic(resultSet);

            close(statement);

            statement = connection.prepareStatement(SELECT_TASK_WITH_DELAY);
            statement.setLong(1, id);
            result.putAll(mapStatistic(statement.executeQuery()));
            statement.setLong(1, id);

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
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
            return mapStatistic(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }


    @Override
    public List<Project> getAllNameAndIdByCustomerId(long employeeId) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_CUSTOMER_ID);
            statement.setLong(1, employeeId);
            resultSet = statement.executeQuery();
            List<Project> result = mapProjectNameAndId(resultSet);
            close(statement);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }


    static LinkedHashMap<String, String> mapStatistic(ResultSet resultSet) throws SQLException {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        while(resultSet.next()) {
            result.put(resultSet.getString(1), resultSet.getString(2));
        }
        return result;
    }


    private List<ProjectDto> mapDtos(ResultSet resultSet) throws SQLException {
        List<ProjectDto> result = new ArrayList<>();
        while (resultSet.next()) {
            ProjectDto project = new ProjectDto();
            project.setProjectId(resultSet.getString("project_id"));
            project.setName(resultSet.getString("name"));
            project.setStartDate(resultSet.getDate("start_date"));
            project.setCompletionDate(resultSet.getDate("completion_date"));
            result.add(project);
        }
        return result;
    }


    @Override
    public Project getById(long id) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            Project result = mapProject(resultSet);
            close(statement);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public List<Project> getAllNameAndId() {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
            resultSet = statement.executeQuery();
            List<Project> result = mapProjectNameAndId(resultSet);
            close(statement);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public int delete(long id) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID);
            statement.setLong(1, id);
            int rows = statement.executeUpdate();
            connection.commit();
            close(statement);
            return rows;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection);
        }
        return  0;
    }

    private List<Project> mapProjectNameAndId(ResultSet resultSet) throws SQLException {
        List<Project> projects = new ArrayList<>();
        while (resultSet.next()) {
            Project project = new Project();
            project.setName(resultSet.getString("name"));
            project.setProjectId(resultSet.getLong("project_id"));
            projects.add(project);
        }
        return projects;
    }

    private ProjectDto mapProjectDto(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            ProjectDto project = new ProjectDto();
            try {
                project.setName(resultSet.getString("name"));
                project.setStartDate(resultSet.getDate("start_date"));
                project.setCompletionDate(resultSet.getDate("completion_date"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return project;
        } else {
            return null;
        }
    }

    private Project mapProject(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            Project project = new Project();
            try {
                long projectId = resultSet.getLong("project_id");
                project.setProjectId(projectId);
                project.setName(resultSet.getString("name"));
                project.setStartDate(resultSet.getDate("start_date"));
                project.setCompletionDate(resultSet.getDate("completion_date"));
                project.setPredicatedCompletionDate(resultSet.getDate("predicated_completion_date"));
                project.setCustomer(employeeDao.getById(resultSet.getLong("customer")));
                project.setManager(employeeDao.getById(resultSet.getLong("manager")));
                project.setSprints(sprintDao.getByProjectId(projectId));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return project;
        } else {
            return null;
        }
    }
}
