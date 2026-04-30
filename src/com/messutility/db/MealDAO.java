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

    public static java.util.List<com.messutility.models.tracker.MealLog> getAllMealLogs() {
        java.util.List<com.messutility.models.tracker.MealLog> logs = new java.util.ArrayList<>();
        String query = "SELECT * FROM meal_logs";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String resId = rs.getString("resident_id");
                String dateStr = rs.getString("log_date");
                int b = rs.getInt("breakfast");
                int l = rs.getInt("lunch");
                int d = rs.getInt("dinner");

                com.messutility.models.users.Resident resident = UserDAO.getResidentById(resId);
                java.util.Date date = dateStr != null ? sdf.parse(dateStr) : new java.util.Date();

                if (resident != null) {
                    logs.add(new com.messutility.models.tracker.MealLog(resident, date, b, l, d));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }

    public static java.util.Map<String, com.messutility.models.tracker.MealLog> getMealLogsForDate(String date) {
        java.util.Map<String, com.messutility.models.tracker.MealLog> map = new java.util.HashMap<>();
        String query = "SELECT * FROM meal_logs WHERE log_date = ?";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, date);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String resId = rs.getString("resident_id");
                    int b = rs.getInt("breakfast");
                    int l = rs.getInt("lunch");
                    int d = rs.getInt("dinner");

                    com.messutility.models.users.Resident resident = UserDAO.getResidentById(resId);
                    java.util.Date parsedDate = sdf.parse(date);

                    if (resident != null) {
                        map.put(resId, new com.messutility.models.tracker.MealLog(resident, parsedDate, b, l, d));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static java.util.Map<String, Integer> getDailyMealTotals() {
        java.util.Map<String, Integer> totals = new java.util.HashMap<>();
        String query = "SELECT log_date, SUM(breakfast + lunch + dinner) AS total FROM meal_logs GROUP BY log_date";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                totals.put(rs.getString("log_date"), rs.getInt("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totals;
    }
}
