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

        centerPanel.add(info);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(contactInfo);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(message);

        add(centerPanel, BorderLayout.CENTER);
    }
}
