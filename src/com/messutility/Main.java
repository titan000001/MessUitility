package com.messutility;

import com.formdev.flatlaf.FlatDarkLaf;
import com.messutility.core.DatabaseManager;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize SQLite Database Tables
        DatabaseManager.initializeDatabase();

        // 2. Set up the modern FlatLaf UI Theme
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize Modern UI Theme");
        }

        // 3. Launch GUI
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Smart Hostel/Mess Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null); // Center window

            // Initialize Default Admin Account if DB is empty
            com.messutility.db.UserDAO.initializeDefaultAdmin();

            // Set up Login Screen
            com.messutility.gui.LoginScreen loginScreen = new com.messutility.gui.LoginScreen(frame);
            frame.add(loginScreen);

            frame.setVisible(true);
        });
    }
}
