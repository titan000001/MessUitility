package com.messutility.gui;

import com.messutility.db.UserDAO;
import com.messutility.models.users.Manager;
import com.messutility.models.users.Resident;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.UUID;

public class ManagerDashboard extends JPanel {
    private Manager manager;

    public ManagerDashboard(Manager manager) {
        this.manager = manager;
        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("Manager Dashboard - Welcome, " + manager.getName());
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(headerLabel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        tabbedPane.addTab("Residents", createResidentsTab());
        tabbedPane.addTab("Meal Tracker", createMealTrackerTab());
        tabbedPane.addTab("Expenses & Billing", createExpensesTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createResidentsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        String[] columns = {"ID", "Name", "Role", "Contact"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addResBtn = new JButton("Add New Resident");
        addResBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addResBtn.setBackground(new Color(46, 204, 113));
        addResBtn.setForeground(Color.WHITE);
        
        addResBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter Resident Name:");
            if (name != null && !name.trim().isEmpty()) {
                String id = "R_" + UUID.randomUUID().toString().substring(0, 5);
                Resident r = new Resident(id, name, "N/A", "password123");
                UserDAO.addUser(r, "RESIDENT");
                model.addRow(new Object[]{id, name, "RESIDENT", "N/A"});
                JOptionPane.showMessageDialog(this, "Resident added! Default password is 'password123'");
            }
        });

        JButton refreshBtn = new JButton("Refresh List");
        // TODO: Load from DB in next iteration
        
        btnPanel.add(refreshBtn);
        btnPanel.add(addResBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMealTrackerTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        java.util.List<Resident> residents = UserDAO.getAllResidents();
        JComboBox<String> residentBox = new JComboBox<>();
        for (Resident r : residents) {
            residentBox.addItem(r.getId() + " - " + r.getName());
        }

        JTextField dateField = new JTextField(java.time.LocalDate.now().toString(), 10);
        JSpinner brkSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner lunSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner dinSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));

        formPanel.add(new JLabel("Resident:"));
        formPanel.add(residentBox);
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Breakfast:"));
        formPanel.add(brkSpinner);
        formPanel.add(new JLabel("Lunch:"));
        formPanel.add(lunSpinner);
        formPanel.add(new JLabel("Dinner:"));
        formPanel.add(dinSpinner);

        JButton saveBtn = new JButton("Save Meal Log");
        saveBtn.setBackground(new Color(52, 152, 219));
        saveBtn.setForeground(Color.WHITE);
        
        saveBtn.addActionListener(e -> {
            if (residentBox.getSelectedItem() != null) {
                String selected = (String) residentBox.getSelectedItem();
                String resId = selected.split(" - ")[0];
                String date = dateField.getText();
                int b = (int) brkSpinner.getValue();
                int l = (int) lunSpinner.getValue();
                int d = (int) dinSpinner.getValue();

                com.messutility.db.MealDAO.logOrUpdateMeal(resId, date, b, l, d);
                JOptionPane.showMessageDialog(this, "Meal logged successfully for " + date);
            }
        });

        formPanel.add(saveBtn);
        panel.add(formPanel, BorderLayout.NORTH);

        // Placeholder for a table to show the daily logs
        JLabel infoLabel = new JLabel("Meal records saved to SQLite 'meal_logs' table.", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        panel.add(infoLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createExpensesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Expense Ledger Module Coming Soon...", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}
