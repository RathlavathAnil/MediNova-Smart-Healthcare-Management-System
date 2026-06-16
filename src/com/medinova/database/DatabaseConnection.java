package com.medinova.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection - Singleton class that manages the MySQL JDBC connection.
 * Ensures only one connection instance is used across the application.
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/medinova";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "XXXX"; 

    private static Connection connection = null;

    // Private constructor - prevents direct instantiation
    private DatabaseConnection() {}

    /**
     * Returns the singleton Connection instance.
     * Creates a new connection if one doesn't exist or is closed.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("[DB] Connected to MediNova database successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] MySQL JDBC Driver not found. Add mysql-connector-j to your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Closes the database connection gracefully.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}