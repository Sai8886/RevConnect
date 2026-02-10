package com.revconnect.application;

import java.sql.Connection;
import java.sql.SQLException;

import com.revconnect.utility.DBConnectionUtil;

public class TestDBConnection {
    
    public void testConnection() {
        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("SUCCESS: Database connection established!");
                System.out.println("Connected to: " + conn.getMetaData().getURL());
                System.out.println("Database: " + conn.getCatalog());
                System.out.println("Driver: " + conn.getMetaData().getDriverName());
            } else {
                System.out.println("FAILED: Could not establish database connection!");
            }
            
        } catch (SQLException e) {
            System.out.println("ERROR: Database connection failed!");
            System.out.println("Reason: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Warning: Failed to close connection");
                }
            }
        }
    }
}