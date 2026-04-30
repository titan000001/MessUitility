package com.messutility.db;

import com.messutility.core.DatabaseManager;
import com.messutility.models.users.Manager;
import com.messutility.models.users.Resident;
import com.messutility.models.users.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static void initializeDefaultAdmin() {
        String countQuery = "SELECT COUNT(*) AS total FROM users";
        String insertQuery = "INSERT INTO users (id, name, role, contact, password) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement countStmt = conn.prepareStatement(countQuery);
             ResultSet rs = countStmt.executeQuery()) {

            if (rs.next() && rs.getInt("total") == 0) {
                // Database is empty, create a default manager
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, "admin");
                    insertStmt.setString(2, "System Manager");
                    insertStmt.setString(3, "MANAGER");
                    insertStmt.setString(4, "N/A");
                    insertStmt.setString(5, "admin123");
                    insertStmt.executeUpdate();
                    System.out.println("Default Admin account created (ID: admin, Pass: admin123)");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static User authenticate(String id, String password) {
        String query = "SELECT * FROM users WHERE id = ? AND password = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setString(1, id);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String role = rs.getString("role");
                    String contact = rs.getString("contact");
                    
                    if ("MANAGER".equalsIgnoreCase(role)) {
                        return new Manager(id, name, contact, password);
                    } else if ("RESIDENT".equalsIgnoreCase(role)) {
                        return new Resident(id, name, contact, password);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Login failed
    }

    public static void addUser(User user, String role) {
        String query = "INSERT INTO users (id, name, role, contact, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getName());
            stmt.setString(3, role);
            stmt.setString(4, "N/A");
            stmt.setString(5, "password123"); // default password
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Resident> getAllResidents() {
        List<Resident> list = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role IN ('RESIDENT', 'GUEST')";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Resident(rs.getString("id"), rs.getString("name"), rs.getString("contact"), rs.getString("password")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Resident getResidentById(String id) {
        String query = "SELECT * FROM users WHERE id = ? AND role IN ('RESIDENT', 'GUEST')";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Resident(rs.getString("id"), rs.getString("name"), rs.getString("contact"), rs.getString("password"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void linkGuestToHost(String guestId, String residentId) {
        String query = "INSERT OR IGNORE INTO guest_hosts (guest_id, resident_id) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, guestId);
            stmt.setString(2, residentId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getGuestHosts(String guestId) {
        List<String> hosts = new ArrayList<>();
        String query = "SELECT resident_id FROM guest_hosts WHERE guest_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, guestId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    hosts.add(rs.getString("resident_id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hosts;
    }
}
