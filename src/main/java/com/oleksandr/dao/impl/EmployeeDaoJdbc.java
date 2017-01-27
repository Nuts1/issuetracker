package com.oleksandr.dao.impl;

import com.oleksandr.dao.*;
import com.oleksandr.dto.*;
import com.oleksandr.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.oleksandr.dao.Util.close;
import static com.oleksandr.dao.Util.rollBack;

/**
 * Created by Nuts on 1/5/2017
 * 3:40 PM.
 */
@Repository
public class EmployeeDaoJdbc implements EmployeeDao {
    @Resource(name = "dataSource")
    private DataSource dataSource;

    private final RoleDao roleDao;

    private final PositionDao positionDao;

    private final QualificationDao qualification;

    private final DepartmentDao departmentDao;

    private static final String SELECT_BY_ROLE = "SELECT e.employee_id AS employee_id, " +
            "e.name AS name," +
            "e.password AS password, " +
            "role_id AS role_id," +
            "e.surname AS surname," +
            "e.qualification_id AS qualification_id," +
            "e.position_id as position_id, " +
            "e.email as email, " +
            "e.department_id as department_id " +
            "FROM employee e INNER JOIN role r USING(role_id) " +
            "WHERE role_id = ?";

    private static final String SELECT_BY_ID = "SELECT e.employee_id AS employee_id, " +
            "e.name AS name," +
            "e.password AS password, " +
            "e.role_id AS role_id," +
            "e.surname AS surname," +
            "e.qualification_id AS qualification_id," +
            "e.email as email, " +
            "e.position_id AS position_id, " +
            "e.department_id AS department_id " +
            "FROM employee e " +
            "WHERE e.employee_id = ?";


    private static final String SELECT_BY_IDS = "SELECT e.employee_id AS employee_id, " +
            "e.name AS name," +
            "e.password AS password, " +
            "e.role_id AS role_id," +
            "e.email as email, " +
            "e.surname AS surname," +
            "e.qualification_id AS qualification_id," +
            "e.position_id AS position_id, " +
            "e.department_id AS department_id " +
            "FROM employee e " +
            "WHERE e.employee_id IN ?";

    private static final String SELECT_BY_DEPT_ID_AND_POS_ID = "SELECT DISTINCT e.employee_id AS employee_id, " +
            "e.name AS name," +
            "e.password AS password, " +
            "e.role_id AS role_id," +
            "e.surname AS surname," +
            "e.qualification_id AS qualification_id," +
            "e.position_id AS position_id, " +
            "e.email as email, " +
            "e.department_id AS department_id " +
            "FROM employee e " +
            "WHERE (e.position_id = ? OR ? IS NULL) AND " +
            "(e.department_id = ?  OR ? IS NULL)";

    private static final String SELECT_BY_PROJECT_ID = "SELECT DISTINCT employee_id AS employee_id, " +
            "e.name AS name," +
            "e.password AS password, " +
            "e.role_id AS role_id," +
            "e.email as email, " +
            "e.surname AS surname," +
            "e.qualification_id AS qualification_id," +
            "e.position_id AS position_id, " +
            "e.department_id AS department_id " +
            "FROM employee e INNER JOIN task_employee te USING(employee_id) " +
            " INNER JOIN task t USING(task_id) " +
            " INNER JOIN sprint s USING(sprint_id) " +
            " INNER JOIN project p USING(project_id) " +
            "WHERE project_id = ?";

    private static final String SELECT_BY_PROJECT_ID_AND_EMPLOYEE_ID = "SELECT DISTINCT employee_id AS employee_id, " +
            "e.name AS name," +
            "e.password AS password, " +
            "e.role_id AS role_id," +
            "e.email as email, " +
            "e.surname AS surname," +
            "e.qualification_id AS qualification_id," +
            "e.position_id AS position_id, " +
            "e.department_id AS department_id " +
            "FROM employee e INNER JOIN task_employee te USING(employee_id) " +
            " INNER JOIN task t USING(task_id) " +
            " INNER JOIN sprint s USING(sprint_id) " +
            " INNER JOIN project p USING(project_id) " +
            "WHERE project_id = ? AND employee_id = ?";


    private static final String SELECT_BY_TASK_ID = "SELECT employee_id AS employee_id, " +
            "e.name AS name," +
            "e.password AS password, " +
            "e.email as email, " +
            "e.role_id AS role_id," +
            "e.surname AS surname," +
            "e.qualification_id AS qualification_id," +
            "e.position_id AS position_id, " +
            "e.department_id AS department_id " +
            "FROM employee e INNER JOIN task_employee te USING(employee_id) " +
            "WHERE te.task_id = ?";

    private static final String SELECT_ALL = "SELECT employee_id AS employee_id, " +
            "e.name AS name," +
            "e.password AS password, " +
            "e.email as email, " +
            "e.role_id AS role_id," +
            "e.surname AS surname," +
            "e.qualification_id AS qualification_id," +
            "e.position_id AS position_id, " +
            "e.department_id AS department_id " +
            "FROM employee e";


    private static final String SELECT_BY_EMAIL = "SELECT e.employee_id AS employee_id, " +
            "e.name AS name," +
            "e.password AS password, " +
            "e.role_id AS role_id," +
            "e.surname AS surname," +
            "e.email as email, " +
            "e.qualification_id AS qualification_id," +
            "e.position_id AS position_id, " +
            "e.department_id AS department_id " +
            "FROM employee e " +
            "WHERE LOWER(e.email) = LOWER(?)";


    private static final String SAVE = "INSERT INTO employee " +
            "(employee_id, password, name, surname , email, role_id, position_id, qualification_id)" +
            " VALUES ( nextval('seq') , '1111' , ?, ?, ?, ?, ?, ?)";

    private static final String update = "UPDATE employee " +
            "SET name = ?," +
            "surname = ?," +
            "email = ?, " +
            "role_id = ?, " +
            "position_id = ?, " +
            "qualification_id = ? " +
            "WHERE employee_id = ?";

    private static final String CHANGE_PASSWORD = "UPDATE employee " +
            "SET password = ?" +
            "WHERE employee_id = ?";


    private static final String DELETE_BY_ID = "DELETE FROM employee WHERE employee_id = ?";


    @Override
    public int save(EmployeeDto employeeDto) {
        Connection connection = null;
        System.out.println(employeeDto);
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(SAVE);
            statement.setString(1, employeeDto.getName());
            statement.setString(2, employeeDto.getSurname());
            statement.setString(3, employeeDto.getEmail());
            statement.setLong(4, Long.parseLong(employeeDto.getRoleId()));
            statement.setLong(5, Long.parseLong(employeeDto.getPositionId()));
            statement.setLong(6, Long.parseLong(employeeDto.getQualificationId()));
            int row = statement.executeUpdate();
            connection.commit();
            close(statement);
            return row;
        } catch (SQLException e) {
            rollBack(connection, e);
            return 0;
        } finally {
            close(connection);
        }
    }

    @Override
    public int update(EmployeeDto employeeDto) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(update);
            statement.setString(1, employeeDto.getName());
            statement.setString(2, employeeDto.getSurname());
            statement.setString(3, employeeDto.getEmail());
            statement.setLong(4, Long.parseLong(employeeDto.getRoleId()));
            statement.setLong(5, Long.parseLong(employeeDto.getPositionId()));
            statement.setLong(6, Long.parseLong(employeeDto.getQualificationId()));
            statement.setLong(7, Long.parseLong(employeeDto.getId()));
            int row = statement.executeUpdate();
            connection.commit();
            close(statement);
            return row;
        } catch (SQLException e) {
            rollBack(connection, e);
            return 0;
        } finally {
            close(connection);
        }
    }

    @Override
    public int delete(long id) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID);
            statement.setLong(1, id);
            int row = statement.executeUpdate();
            close(statement);
            return row;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            close(connection);
        }
    }


    @Autowired
    public EmployeeDaoJdbc(RoleDao roleDao, PositionDao positionDao, QualificationDao qualification, DepartmentDao departmentDao) {
        this.roleDao = roleDao;
        this.positionDao = positionDao;
        this.qualification = qualification;
        this.departmentDao = departmentDao;
    }

    @Override
    public Employee getById(long id) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            Employee employee = mapEmployee(resultSet);
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
    public Employee getEmployeeByIds(Long[] id) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_IDS);
            Array ids = connection.createArrayOf("NUMBER", id);
            statement.setArray(1, ids);
            resultSet = statement.executeQuery();
            Employee employee = mapEmployee(resultSet);
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
    public Employee getEmployeeByEmail(String email) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_EMAIL);
            statement.setString(1, email);
            resultSet = statement.executeQuery();
            Employee employee = mapEmployee(resultSet);
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
    public List<Employee> getEmployeeFromTask(long id) {
        return getByLongParam(id, SELECT_BY_TASK_ID);
    }

    @Override
    public List<Employee> getByProjectId(long idProject) {
        return getByLongParam(idProject, SELECT_BY_PROJECT_ID);
    }

    @Override
    public List<Employee> getByProjectIdAndEmployeeId(long idProject, long idEmployee) {
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_PROJECT_ID_AND_EMPLOYEE_ID);
            statement.setLong(1, idProject);
            statement.setLong(2, idEmployee);
            resultSet = statement.executeQuery();
            List<Employee> employees = mapEmployees(resultSet);
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
    public List<Employee> getByRoleId(long role) {
        return getByLongParam(role, SELECT_BY_ROLE);
    }

    private List<Employee> getByLongParam(long id, String sql) {
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            List<Employee> employees = mapEmployees(resultSet);
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
    public List<Employee> getByDeptIdAndPosId(Long idDep, Long idPo) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_DEPT_ID_AND_POS_ID);
            if(idPo != null) {
                statement.setLong(1, idPo);
                statement.setLong(2, idPo);
            } else {
                statement.setNull(1, Types.BIGINT);
                statement.setNull(2, Types.BIGINT);
            }
            if(idDep != null) {
                statement.setLong(3, idDep);
                statement.setLong(4, idDep);
            } else {
                statement.setNull(3, Types.BIGINT);
                statement.setNull(4, Types.BIGINT);
            }
            resultSet = statement.executeQuery();
            List<Employee> employees = mapEmployees(resultSet);
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
    public boolean changePassword(String newPassHash, long id) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(CHANGE_PASSWORD);
            statement.setString(1, newPassHash);
            statement.setLong(2, id);
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

    @Override
    public List<Employee> getAll() {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(SELECT_ALL);
            List<Employee> employees = mapEmployees(resultSet);
            close(statement);
            return employees;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, resultSet);
        }
    }

    private List<Employee> mapEmployees(ResultSet resultSet) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        while (resultSet.next()) {
            Employee employee = new Employee();
            map(resultSet, employee);
            employees.add(employee);
        }
        return employees;
    }

    private Employee mapEmployee(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            Employee employee = new Employee();
            map(resultSet, employee);
            return employee;
        }
        return null;
    }

    private void map(ResultSet resultSet, Employee employee) {
        try {
            employee.setName(resultSet.getString("name"));
            employee.setSurname(resultSet.getString("surname"));
            employee.setPassword(resultSet.getString("password"));
            employee.setEmployeeId(resultSet.getLong("employee_id"));
            employee.setEmail(resultSet.getString("email"));
            employee.setRole(roleDao.getById(resultSet.getLong("role_id")));
            employee.setQualification(qualification.getById(resultSet.getLong("qualification_id")));
            employee.setPosition(positionDao.getById(resultSet.getLong("position_id")));
            employee.setDepartment(departmentDao.getById(resultSet.getLong("department_id")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}