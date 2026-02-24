package com.oceanview.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton pattern: Ensures only one database connection instance exists.
 * Uses lazy initialisation with thread safety via a synchronized accessor.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private static final String DB_URL      = "jdbc:mysql://localhost:3306/oceanview_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    private DatabaseConnection() {}

    /** Returns the single instance of DatabaseConnection (Singleton). */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /** Returns a valid connection, reconnecting if necessary. */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        }
        return connection;
    }

    /** Initialises the JDBC driver and opens the first connection. */
    public void initialize() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            getConnection();
            System.out.println("[DB] Connection established to oceanview_db");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] MySQL JDBC driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DB] Connection failed: " + e.getMessage());
            System.err.println("[DB] Application will start; some features may be unavailable.");
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}
