package com.revconnect.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.revconnect.configuration.DataBaseConfiguration;

public class DBConnectionUtil {

    static {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL JDBC Driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DataBaseConfiguration.DB_URL,
                DataBaseConfiguration.DB_USERNAME,
                DataBaseConfiguration.DB_PASSWORD
        );
    }

    public static void closeDataSource() {
        // Not needed for plain JDBC (kept for compatibility)
    }

    private DBConnectionUtil() {
        // Prevent instantiation
    }
}
