package com.messutility.gui;

import com.messutility.models.users.Resident;
import javax.swing.*;
import java.awt.*;

public class ResidentDashboard extends JPanel {
    public ResidentDashboard(Resident resident) {
        setLayout(new BorderLayout());
        
        // Header
        JLabel headerLabel = new JLabel("Resident Dashboard - Welcome, " + resident.getName());
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(headerLabel, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel info = new JLabel("Your Resident ID: " + resident.getId());
        info.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        JLabel contactInfo = new JLabel("Contact: " + resident.getContact());
        contactInfo.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        JLabel message = new JLabel("<html><br><br><b>Notice:</b> Your personal meal history and month-end generated bills will appear here in the next update. Please contact your Manager for this month's dues.</html>");
        message.setFont(new Font("Segoe UI", Font.ITALIC, 16));

        // Guest Management Section
        JPanel guestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        guestPanel.setBorder(BorderFactory.createTitledBorder("Guest Management"));
        
        JButton createGuestBtn = new JButton("Register New Guest");
        createGuestBtn.setBackground(new Color(155, 89, 182));
        createGuestBtn.setForeground(Color.WHITE);
        createGuestBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter New Guest Name:");
            if (name != null && !name.trim().isEmpty()) {
                String guestId = "G_" + java.util.UUID.randomUUID().toString().substring(0, 5);
                Resident g = new Resident(guestId, name, "N/A", "guestpass");
                com.messutility.db.UserDAO.addUser(g, "GUEST");
                com.messutility.db.UserDAO.linkGuestToHost(guestId, resident.getId());
                JOptionPane.showMessageDialog(this, "Guest " + name + " registered and linked to you!\nGuest ID: " + guestId + "\nShare this ID with other residents if they want to co-host.");
            }
        });

        JButton linkGuestBtn = new JButton("Co-Host Existing Guest");
        linkGuestBtn.setBackground(new Color(41, 128, 185));
        linkGuestBtn.setForeground(Color.WHITE);
        linkGuestBtn.addActionListener(e -> {
            String guestId = JOptionPane.showInputDialog(this, "Enter Existing Guest ID (e.g. G_1a2b3):");
            if (guestId != null && guestId.startsWith("G_")) {
                com.messutility.db.UserDAO.linkGuestToHost(guestId, resident.getId());
                JOptionPane.showMessageDialog(this, "You are now a co-host for " + guestId);
            }
        });

        guestPanel.add(createGuestBtn);
        guestPanel.add(linkGuestBtn);

        centerPanel.add(info);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(contactInfo);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(guestPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(message);

        add(centerPanel, BorderLayout.CENTER);
    }
}
