package com.messutility.gui;

import com.messutility.db.UserDAO;
import com.messutility.models.users.Manager;
import com.messutility.models.users.User;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JPanel {
    private JFrame parentFrame;

    public LoginScreen(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        formPanel.setPreferredSize(new Dimension(350, 250));

        JLabel titleLabel = new JLabel("Hostel/Mess Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField idField = new JTextField();
        idField.setBorder(BorderFactory.createTitledBorder("User ID"));
        idField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JPasswordField passField = new JPasswordField();
        passField.setBorder(BorderFactory.createTitledBorder("Password"));
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.setBackground(new Color(41, 128, 185));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String pass = new String(passField.getPassword());

            if (id.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both ID and Password.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User user = UserDAO.authenticate(id, pass);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Welcome back, " + user.getName() + "!", "Login Success", JOptionPane.INFORMATION_MESSAGE);
                
                parentFrame.getContentPane().removeAll();
                if (user instanceof Manager) {
                    parentFrame.add(new ManagerDashboard((Manager) user));
                } else if (user instanceof com.messutility.models.users.Resident) {
                    parentFrame.add(new ResidentDashboard((com.messutility.models.users.Resident) user));
                }
                parentFrame.revalidate();
                parentFrame.repaint();

            } else {
                JOptionPane.showMessageDialog(this, "Invalid User ID or Password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(titleLabel);
        formPanel.add(idField);
        formPanel.add(passField);
        formPanel.add(loginBtn);

        add(formPanel);
    }
}
