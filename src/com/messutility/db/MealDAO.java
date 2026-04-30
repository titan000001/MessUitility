package com.messutility.db;

import com.messutility.core.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MealDAO {

    public static void logOrUpdateMeal(String residentId, String date, int breakfast, int lunch, int dinner) {
        String checkQuery = "SELECT id FROM meal_logs WHERE resident_id = ? AND log_date = ?";
        String updateQuery = "UPDATE meal_logs SET breakfast = ?, lunch = ?, dinner = ? WHERE id = ?";
        String insertQuery = "INSERT INTO meal_logs (resident_id, log_date, breakfast, lunch, dinner) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            
            checkStmt.setString(1, residentId);
            checkStmt.setString(2, date);
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // Update existing
                    int logId = rs.getInt("id");
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, breakfast);
                        updateStmt.setInt(2, lunch);
                        updateStmt.setInt(3, dinner);
                        updateStmt.setInt(4, logId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Insert new
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, residentId);
                        insertStmt.setString(2, date);
                        insertStmt.setInt(3, breakfast);
                        insertStmt.setInt(4, lunch);
                        insertStmt.setInt(5, dinner);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
