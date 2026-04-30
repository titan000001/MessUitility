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

        class UserItem {
            User user;
            UserItem(User u) { this.user = u; }
            public String toString() { return user.getName() + " (" + user.getClass().getSimpleName() + ")"; }
        }

        JComboBox<UserItem> userBox = new JComboBox<>();
        for (User u : UserDAO.getAllUsers()) {
            userBox.addItem(new UserItem(u));
        }
        userBox.setBorder(BorderFactory.createTitledBorder("Select User"));
        userBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JPasswordField passField = new JPasswordField();
        passField.setBorder(BorderFactory.createTitledBorder("Password"));
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.setBackground(new Color(41, 128, 185));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover animation effect
        loginBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginBtn.setBackground(new Color(52, 152, 219));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginBtn.setBackground(new Color(41, 128, 185));
            }
        });

        loginBtn.addActionListener(e -> {
            UserItem selectedItem = (UserItem) userBox.getSelectedItem();
            if (selectedItem == null) {
                JOptionPane.showMessageDialog(this, "No user selected.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String id = selectedItem.user.getId();
            String pass = new String(passField.getPassword());

            if (pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your Password.", "Warning", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Invalid Password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(titleLabel);
        formPanel.add(userBox);
        formPanel.add(passField);
        formPanel.add(loginBtn);

        add(formPanel);
    }
}
