package com.messutility.gui;

import com.messutility.core.DatabaseManager;
import com.messutility.db.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class MessSelectionScreen extends JPanel {
    private JFrame parentFrame;

    public MessSelectionScreen(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new GridBagLayout());
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Select Workspace (Mess/Month)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Scan for .db files in user data directory
        java.util.List<String> dbFiles = new ArrayList<>();
        File currentDir = new File(DatabaseManager.getAppDataFolder());
        File[] files = currentDir.listFiles((dir, name) -> name.endsWith(".db"));
        if (files != null) {
            for (File f : files) {
                dbFiles.add(f.getName().replace(".db", ""));
            }
        }
        
        JComboBox<String> dbDropdown = new JComboBox<>(dbFiles.toArray(new String[0]));
        dbDropdown.setMaximumSize(new Dimension(300, 30));
        dbDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton openBtn = new JButton("Open Workspace");
        openBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        openBtn.setBackground(new Color(41, 128, 185));
        openBtn.setForeground(Color.WHITE);
        openBtn.addActionListener(e -> {
            String selected = (String) dbDropdown.getSelectedItem();
            if (selected != null) {
                DatabaseManager.setDatabase(selected);
                proceedToLogin();
            }
        });

        JLabel orLabel = new JLabel("- OR -");
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton newBtn = new JButton("Create New Workspace");
        newBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        newBtn.setBackground(new Color(46, 204, 113));
        newBtn.setForeground(Color.WHITE);
        newBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter Workspace Name (e.g., BoysHostel_Jan2026):");
            if (name != null && !name.trim().isEmpty()) {
                // Ensure valid filename (alphanumeric and underscores)
                name = name.replaceAll("[^a-zA-Z0-9_-]", "_");
                
                JTextField adminNameField = new JTextField();
                JPasswordField adminPassField = new JPasswordField();
                Object[] message = {
                    "Admin Name (Your Name):", adminNameField,
                    "Admin Password:", adminPassField
                };
                
                int option = JOptionPane.showConfirmDialog(this, message, "Setup Administrator Account", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    DatabaseManager.setDatabase(name);
                    DatabaseManager.initializeDatabase(); // Create tables immediately
                    
                    String adminName = adminNameField.getText().trim();
                    String adminPass = new String(adminPassField.getPassword());
                    if (adminName.isEmpty()) adminName = "System Manager";
                    if (adminPass.isEmpty()) adminPass = "admin123";
                    
                    com.messutility.models.users.Manager manager = new com.messutility.models.users.Manager("admin", adminName, "N/A", adminPass);
                    UserDAO.addUser(manager, "MANAGER");
                    
                    // Automatically add the Admin as a Resident for meal tracking
                    com.messutility.models.users.Resident adminRes = new com.messutility.models.users.Resident("R_admin", adminName + " (Admin)", "N/A", adminPass);
                    UserDAO.addUser(adminRes, "RESIDENT");
                    
                    proceedToLogin();
                }
            }
        });

        centerPanel.add(title);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(new JLabel("Existing Workspaces:"));
        centerPanel.add(dbDropdown);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(openBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(orLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(newBtn);

        add(centerPanel);
    }

    private void proceedToLogin() {
        // Initialize the selected database (creates tables if new)
        DatabaseManager.initializeDatabase();
        
        // Ensure default admin is created in this specific database
        UserDAO.initializeDefaultAdmin();

        parentFrame.getContentPane().removeAll();
        parentFrame.add(new LoginScreen(parentFrame));
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}
