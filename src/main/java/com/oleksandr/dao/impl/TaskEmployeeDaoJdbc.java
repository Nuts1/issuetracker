package com.oleksandr.dao.impl;

import com.oleksandr.dao.*;
import com.oleksandr.entity.Employee;
import com.oleksandr.entity.Task;
import com.oleksandr.entity.TaskEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.oleksandr.dao.Util.close;

/**
 * Created by Nuts on 1/10/2017
 * 9:59 PM.
 */
@Repository
public class TaskEmployeeDaoJdbc implements TaskEmployeeDao {

    @Resource(name = "dataSource")
    private DataSource dataSource;

    private final TaskDao taskDao;
    private final QualificationDao qualificationDao;
    private final DepartmentDao departmentDao;
    private final RoleDao roleDao;
    private final PositionDao positionDao;

    private static final String TASK_EMPLOYEE_BY_ID_TASK_AND_ID_EMPLOYEE = "SELECT employee_id AS employee_id, " +
            "e.name AS name," +
            "e.password AS password, " +
            "e.role_id AS role_id," +
            "e.surname AS surname," +
            "e.qualification_id AS qualification_id," +
            "e.position_id AS position_id, " +
            "e.email AS email, " +
            "e.department_id AS department_id, " +
            "te.id_task_employee AS id_task_employee, " +
            "task_id AS task_id, " +
            "te.load AS load, " +
            "te.confirm AS confirm, " +
            "p.name AS projectName, " +
            "s.name AS sprintName " +
            "FROM employee e INNER JOIN task_employee te USING(employee_id) " +
            "INNER JOIN task t USING(task_id) " +
            "INNER JOIN sprint s USING(sprint_id) " +
            "INNER JOIN project p USING(project_id) " +
            "WHERE task_id = ? AND employee_id = ?";

    private static final String SELECT_EMPLOYEE_TASKS_BY_EMPLOYEE_AND_PROJECT = "SELECT employee_id AS employee_id, " +
            "e.name AS name," +
            "e.password AS password, " +
            "e.role_id AS role_id," +
            "e.surname AS surname," +
            "e.qualification_id AS qualification_id," +
            "e.position_id AS position_id, " +
            "e.email AS email, " +
            "e.department_id AS department_id, " +
            "te.id_task_employee AS id_task_employee, " +
            "task_id AS task_id, " +
            "te.confirm AS confirm, " +
            "p.name AS projectName, " +
            "s.name AS sprintName " +
            "FROM employee e INNER JOIN task_employee te USING(employee_id) " +
            "INNER JOIN task t USING(task_id) " +
            "INNER JOIN sprint s USING(sprint_id) " +
            "INNER JOIN project p USING(project_id) " +
            "WHERE employee_id = ? AND project_id = ?";

    private static final String SELECT_EMPLOYEE_TASKS_BY_EMPLOYEE_AND_SPRINT = "SELECT employee_id AS employee_id, " +
            "e.name AS name," +
            "e.password AS password, " +
            "e.role_id AS role_id," +
            "e.surname AS surname," +
            "e.qualification_id AS qualification_id," +
            "e.position_id AS position_id, " +
            "e.email AS email, " +
            "e.department_id AS department_id, " +
            "te.id_task_employee AS id_task_employee, " +
            "task_id AS task_id, " +
            "load AS load, " +
            "te.confirm AS confirm, " +
            "p.name AS projectName, " +
            "s.name AS sprintName " +
            "FROM employee e INNER JOIN task_employee te USING(employee_id) " +
            "INNER JOIN task t USING(task_id) " +
            "INNER JOIN sprint s USING(sprint_id) " +
            "INNER JOIN project p USING(project_id) " +
            "WHERE employee_id = ? AND sprint_id = ?";

    private static final String TASK_EMPLOYEE_BY_EMPLOYEE_ID = "SELECT employee_id AS employee_id, " +
            "e.name AS name," +
            "e.password AS password, " +
            "e.role_id AS role_id," +
            "e.surname AS surname," +
            "e.qualification_id AS qualification_id," +
            "e.position_id AS position_id, " +
            "e.email AS email, " +
            "e.department_id AS department_id, " +
            "te.id_task_employee AS id_task_employee, " +
            "task_id AS task_id, " +
            "te.confirm AS confirm, " +
            "p.name AS projectName, " +
            "s.name AS sprintName " +
            "FROM employee e INNER JOIN task_employee te USING(employee_id) " +
            "INNER JOIN task t USING(task_id) " +
            "INNER JOIN sprint s USING(sprint_id) " +
            "INNER JOIN project p USING(project_id) " +
            "WHERE employee_id = ?";

    private static final String TASK_EMPLOYEE_BY_EMPLOYEE = "SELECT " +
            "te.id_task_employee AS id_task_employee, " +
            "te.confirm AS confirm, " +
            "task_id AS task_id, " +
            "te.load AS load, " +
            " p.name AS projectName, " +
            "s.name AS sprintName " +
            "FROM employee e INNER JOIN task_employee te USING(employee_id) " +
            "INNER JOIN task t USING(task_id) " +
            "INNER JOIN sprint s USING(sprint_id) " +
            "INNER JOIN project p USING(project_id) " +
            "WHERE employee_id = ?";

    private static final String CONFIRM = "UPDATE task_employee " +
            "SET confirm = 1 " +
            "WHERE task_id = ? AND employee_id = ?";


    private static final String SELECT_BY_SPRINT_ID_AND_EMPLOYEE_ID =  "SELECT DISTINCT task_id AS task_id, " +
            "name AS name, " +
            "start_date AS start_date, " +
            "estimate AS estimate, " +
            "sprint_id AS sprint_id, " +
            "subtask_id AS subtask_id, " +
            "previous_task_id AS previous_task_id, " +
            "description AS description, " +
            "completion_date AS completion_date, " +
            "load AS load, " +
            "predicted_delay AS predicted_delay, " +
            "actual_completion_date AS actual_completion_date, " +
            "actual_start_date AS actual_start_date " +
            "FROM task INNER JOIN task_employee USING(task_id)" +
            "WHERE sprint_id = ? AND employee_id = ?";


    @Autowired
    public TaskEmployeeDaoJdbc(TaskDao taskDao,
                               DepartmentDao departmentDao,
                               QualificationDao qualificationDao,
                               RoleDao roleDao,
                               PositionDao positionDao) {
        this.taskDao = taskDao;
        this.departmentDao = departmentDao;
        this.qualificationDao = qualificationDao;
        this.roleDao = roleDao;
        this.positionDao = positionDao;
    }


    @Override
    public List<TaskEmployee> getEmployeeTasksByEmployeeId(long id) {
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(TASK_EMPLOYEE_BY_EMPLOYEE_ID);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            List<TaskEmployee> employees = mapTaskEmployees(resultSet);
            close(statement);
            return employees;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public List<TaskEmployee> getEmployeeTasksByEmployee(Employee employee) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(TASK_EMPLOYEE_BY_EMPLOYEE);
            statement.setLong(1, employee.getEmployeeId());
            resultSet = statement.executeQuery();
            List<TaskEmployee> employees = mapTaskEmployees(resultSet, employee);
            close(statement);
            return employees;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public TaskEmployee getByEmployeeAndTaskId(long taskId, long employeeId) {
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(TASK_EMPLOYEE_BY_ID_TASK_AND_ID_EMPLOYEE);
            statement.setLong(1, taskId);
            statement.setLong(2, employeeId);
            resultSet = statement.executeQuery();
            TaskEmployee employee = null;
            if(resultSet.next()) {
                employee = mapTaskEmployee(resultSet);
            }
            close(statement);
            return employee;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    @Override
    public List<TaskEmployee> getEmployeeTasksByEmployeeAndProject(Employee employee, Long idProject) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_EMPLOYEE_TASKS_BY_EMPLOYEE_AND_PROJECT);
            statement.setLong(1, employee.getEmployeeId());
            statement.setLong(2, idProject);
            resultSet = statement.executeQuery();
            List<TaskEmployee> employees = mapTaskEmployees(resultSet, employee);
            close(statement);
            return employees;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }


    @Override
    public List<TaskEmployee> getEmployeeTasksByEmployeeAndSprint(Employee employee, Long idProject) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_EMPLOYEE_TASKS_BY_EMPLOYEE_AND_SPRINT);
            statement.setLong(1, employee.getEmployeeId());
            statement.setLong(2, idProject);
            resultSet = statement.executeQuery();
            List<TaskEmployee> employees = mapTaskEmployees(resultSet, employee);
            close(statement);
            return employees;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }



    @Override
    public boolean confirm(long taskId, long employeeId) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(CONFIRM);
            statement.setLong(1, taskId);
            statement.setLong(2, employeeId);
            statement.executeUpdate();
            close(statement);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(connection);
        }
    }


    private List<TaskEmployee> mapTaskEmployees(ResultSet resultSet) throws SQLException {
        List<TaskEmployee> employees = new ArrayList<>();
        while (resultSet.next()) {
            employees.add(mapTaskEmployee(resultSet));
        }
        return employees;
    }

    private List<TaskEmployee> mapTaskEmployees(ResultSet resultSet, Employee employee) throws SQLException {
        List<TaskEmployee> employees = new ArrayList<>();
        while (resultSet.next()) {
            TaskEmployee taskEmployee = new TaskEmployee();
            taskEmployee.setEmployee(employee);
            taskEmployee.setIdTaskEmployee(resultSet.getLong("id_task_employee"));
            taskEmployee.setLoad(resultSet.getInt("load"));
            taskEmployee.setConfirm(resultSet.getString("CONFIRM"));
            taskEmployee.setProjectName(resultSet.getString("projectName"));
            taskEmployee.setSprintName(resultSet.getString("sprintName"));
            taskEmployee.setTask(taskDao.getByIdWithoutEmployees(resultSet.getLong("task_id")));
            employees.add(taskEmployee);
        }
        return employees;
    }

    private TaskEmployee mapTaskEmployee(ResultSet resultSet) {
        TaskEmployee taskEmployee = new TaskEmployee();
        try {
            Employee employee = mapEmployee(resultSet);
            taskEmployee.setEmployee(employee);
            taskEmployee.setIdTaskEmployee(resultSet.getLong("id_task_employee"));
            taskEmployee.setLoad(resultSet.getInt("load"));
            taskEmployee.setConfirm(resultSet.getString("CONFIRM"));
            taskEmployee.setTask(taskDao.getByIdWithoutEmployees(resultSet.getLong("task_id")));
            taskEmployee.setProjectName(resultSet.getString("projectName"));
            taskEmployee.setSprintName(resultSet.getString("sprintName"));
            System.out.println(taskEmployee);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taskEmployee;
    }


    private Employee mapEmployee(ResultSet resultSet) {
        try {
            Employee employee = new Employee();
            employee.setEmail(resultSet.getString("email"));
            employee.setName(resultSet.getString("name"));
            employee.setSurname(resultSet.getString("surname"));
            employee.setPassword(resultSet.getString("password"));
            employee.setEmployeeId(resultSet.getLong("employee_id"));
            employee.setRole(roleDao.getById(resultSet.getLong("role_id")));
            employee.setQualification(qualificationDao.getById(resultSet.getLong("qualification_id")));
            employee.setPosition(positionDao.getById(resultSet.getLong("position_id")));
            employee.setDepartment(departmentDao.getById(resultSet.getLong("department_id")));
            return employee;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
