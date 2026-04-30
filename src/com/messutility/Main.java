package com.messutility;

import com.formdev.flatlaf.FlatDarkLaf;
import com.messutility.core.DatabaseManager;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // 1. Set up the modern FlatLaf UI Theme
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize Modern UI Theme");
        }

        // 2. Launch GUI with Workspace Selection Screen
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Smart Hostel/Mess Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null); // Center window

            // Set up Workspace Selection Screen
            com.messutility.gui.MessSelectionScreen selectionScreen = new com.messutility.gui.MessSelectionScreen(frame);
            frame.add(selectionScreen);

            frame.setVisible(true);
        });
    }
}
