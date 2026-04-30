package com.messutility.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseManager {
    public static String getAppDataFolder() {
        String path = System.getProperty("user.home") + "/MessUtilityData";
        java.io.File dir = new java.io.File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }

    private static String currentDbUrl = "jdbc:sqlite:" + getAppDataFolder() + "/default_mess.db";

    public static void setDatabase(String dbName) {
        currentDbUrl = "jdbc:sqlite:" + getAppDataFolder() + "/" + dbName + ".db";
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(currentDbUrl);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Create Users Table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "role TEXT NOT NULL, " +
                    "contact TEXT, " +
                    "password TEXT)");

            // Create MealLogs Table
            stmt.execute("CREATE TABLE IF NOT EXISTS meal_logs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "resident_id TEXT, " +
                    "log_date TEXT, " +
                    "breakfast INTEGER, " +
                    "lunch INTEGER, " +
                    "dinner INTEGER, " +
                    "FOREIGN KEY(resident_id) REFERENCES users(id))");

            // Create Expenses Table
            stmt.execute("CREATE TABLE IF NOT EXISTS expenses (" +
                    "id TEXT PRIMARY KEY, " +
                    "type TEXT, " +
                    "amount REAL, " +
                    "description TEXT, " +
                    "status TEXT, " +
                    "paid_by TEXT, " +
                    "due_date TEXT, " +
                    "FOREIGN KEY(paid_by) REFERENCES users(id))");
                    
            System.out.println("SQLite Database Initialized!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
