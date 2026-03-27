package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Database credentials - stored in one place
    private static final String URL = "jdbc:mysql://localhost:3306/wastepickup";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "I@mr00tus3r1";

    // Single connection instance
    private static Connection connection = null;

    // Private constructor - prevents anyone from creating
    // an instance of this class
    private DBConnection() {
    }

    // The one method everyone uses to get a connection
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(
                        URL, USERNAME, PASSWORD);
                System.out.println("Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return connection;
    }

    // Close connection when application exits
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}