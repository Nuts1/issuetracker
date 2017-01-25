package com.oleksandr.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Nuts on 1/8/2017
 * 4:09 PM.
 */
public class Util {
    public static void close(Statement statement) {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignore) {
                ignore.printStackTrace();
            }
        }
    }

    public static void rollBack(Connection connection, SQLException e) {
        try {
            e.printStackTrace();
            connection.rollback();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public static void close(Connection connection, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException ignore) {
                ignore.printStackTrace();
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {
                try {
                    connection.close();
                } catch (SQLException ignore) {
                    ignore.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
