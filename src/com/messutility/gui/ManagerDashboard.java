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

        // LEFT PANEL: List of Days
        DefaultListModel<String> daysModel = new DefaultListModel<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.YearMonth yearMonth = java.time.YearMonth.from(today);
        for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
            daysModel.addElement(yearMonth.atDay(i).toString());
        }
        
        JList<String> daysList = new JList<>(daysModel);
        daysList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        daysList.setFont(new Font("Segoe UI", Font.BOLD, 14));
        daysList.setSelectedIndex(today.getDayOfMonth() - 1); // select today
        
        JScrollPane leftScroll = new JScrollPane(daysList);
        leftScroll.setPreferredSize(new Dimension(150, 0));
        leftScroll.setBorder(BorderFactory.createTitledBorder("Days of Month"));
        
        panel.add(leftScroll, BorderLayout.WEST);

        // RIGHT PANEL: Table of Residents
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        JLabel dateLabel = new JLabel("Editing Meals for: " + daysList.getSelectedValue());
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rightPanel.add(dateLabel, BorderLayout.NORTH);

        String[] columns = {"Resident ID", "Resident Name", "Breakfast", "Lunch", "Dinner"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 1; // Only B/L/D are editable
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Hide the Resident ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane tableScroll = new JScrollPane(table);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        Runnable loadTableForDate = () -> {
            String selectedDate = daysList.getSelectedValue();
            if (selectedDate == null) return;
            dateLabel.setText("Editing Meals for: " + selectedDate);
            tableModel.setRowCount(0); // clear
            
            java.util.List<Resident> currentResidents = UserDAO.getAllResidents();
            java.util.Map<String, com.messutility.models.tracker.MealLog> logs = com.messutility.db.MealDAO.getMealLogsForDate(selectedDate);
            
            for (Resident r : currentResidents) {
                com.messutility.models.tracker.MealLog log = logs.get(r.getId());
                int b = log != null ? log.getBreakfastCount() : 0;
                int l = log != null ? log.getLunchCount() : 0;
                int d = log != null ? log.getDinnerCount() : 0;
                tableModel.addRow(new Object[]{r.getId(), r.getName(), b, l, d});
            }
        };

        daysList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadTableForDate.run();
            }
        });

        // Initialize table
        loadTableForDate.run();

        // SAVE BUTTON
        JButton saveBtn = new JButton("Save Meals for Selected Day");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        
        saveBtn.addActionListener(e -> {
            if (table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }
            String selectedDate = daysList.getSelectedValue();
            if (selectedDate == null) return;
            
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String rId = tableModel.getValueAt(i, 0).toString();
                String rName = tableModel.getValueAt(i, 1).toString();
                try {
                    int b = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
                    int l = Integer.parseInt(tableModel.getValueAt(i, 3).toString());
                    int d = Integer.parseInt(tableModel.getValueAt(i, 4).toString());
                    
                    com.messutility.db.MealDAO.logOrUpdateMeal(rId, selectedDate, b, l, d);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid number for " + rName);
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Meals successfully saved for " + selectedDate + "!");
        });
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(saveBtn);
        rightPanel.add(btnPanel, BorderLayout.SOUTH);

        panel.add(rightPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createExpensesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Panel for Adding Expense
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Expense"));
        
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"MEAL", "UTILITY"});
        JTextField amountField = new JTextField(5);
        JTextField descField = new JTextField(10);
        
        java.util.List<Resident> residents = UserDAO.getAllResidents();
        JComboBox<String> residentBox = new JComboBox<>();
        residentBox.addItem("None (Manager Paid)");
        for (Resident r : residents) {
            residentBox.addItem(r.getId() + " - " + r.getName());
        }

        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeBox);
        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amountField);
        formPanel.add(new JLabel("Desc:"));
        formPanel.add(descField);
        formPanel.add(new JLabel("Paid By:"));
        formPanel.add(residentBox);

        JButton addBtn = new JButton("Add Expense");
        addBtn.setBackground(new Color(230, 126, 34));
        addBtn.setForeground(Color.WHITE);
        formPanel.add(addBtn);

        // Center panel for displaying bills
        JTextArea billsArea = new JTextArea();
        billsArea.setEditable(false);
        billsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(billsArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Generated Monthly Bills"));

        addBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String desc = descField.getText();
                String type = (String) typeBox.getSelectedItem();
                
                Resident paidBy = null;
                if (residentBox.getSelectedIndex() > 0) {
                    String selected = (String) residentBox.getSelectedItem();
                    String resId = selected.split(" - ")[0];
                    for (Resident r : residents) {
                        if (r.getId().equals(resId)) {
                            paidBy = r;
                            break;
                        }
                    }
                }

                com.messutility.models.expenses.Expense exp;
                if ("UTILITY".equals(type)) {
                    exp = new com.messutility.models.expenses.UtilityBill(null, new java.util.Date(), amount, desc, paidBy, new java.util.Date());
                } else {
                    exp = new com.messutility.models.expenses.MealExpense(null, new java.util.Date(), amount, desc, paidBy, new java.util.Date());
                }
                exp.approve(); // Auto approve for manager
                com.messutility.db.ExpenseDAO.addExpense(exp, type);
                
                JOptionPane.showMessageDialog(this, "Expense Added!");
                amountField.setText("");
                descField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Amount must be a number.");
            }
        });

        // Bottom panel for generating bills
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton generateBtn = new JButton("Generate Month-End Bills");
        generateBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        generateBtn.setBackground(new Color(41, 128, 185));
        generateBtn.setForeground(Color.WHITE);
        bottomPanel.add(generateBtn);

        JButton exportBtn = new JButton("Export to PDF");
        exportBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        exportBtn.setBackground(new Color(155, 89, 182));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setEnabled(false);
        bottomPanel.add(exportBtn);

        java.util.List<com.messutility.core.MonthlyBill> currentBills = new java.util.ArrayList<>();

        generateBtn.addActionListener(e -> {
            java.util.List<com.messutility.core.MonthlyBill> bills = com.messutility.core.SettlementEngine.generateMonthlyBills(
                com.messutility.db.ExpenseDAO.getAllExpenses(), 
                com.messutility.db.MealDAO.getAllMealLogs(), 
                UserDAO.getAllResidents()
            );
            
            currentBills.clear();
            currentBills.addAll(bills);
            exportBtn.setEnabled(true);
            
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-25s | %-15s\n", "Resident Name", "Amount to Pay"));
            sb.append("--------------------------------------------------\n");
            for (com.messutility.core.MonthlyBill b : bills) {
                if (b.getAmountDue() > 0) {
                    sb.append(String.format("%-25s | $%-14.2f\n", b.getResident().getName(), b.getAmountDue()));
                } else {
                    if (b.getResident().getNetBalance() > 0) {
                        sb.append(String.format("%-25s | Owed to them: $%.2f\n", b.getResident().getName(), b.getResident().getNetBalance()));
                    } else {
                        sb.append(String.format("%-25s | $0.00\n", b.getResident().getName()));
                    }
                }
            }
            billsArea.setText(sb.toString());
            JOptionPane.showMessageDialog(this, "Bills Generated successfully!");
        });

        exportBtn.addActionListener(e -> {
            try {
                String folder = com.messutility.core.DatabaseManager.getAppDataFolder();
                String fileName = folder + "/Bills_Report_" + System.currentTimeMillis() + ".pdf";
                com.messutility.core.PDFGenerator.generateMonthlyBillsPDF(currentBills, fileName);
                JOptionPane.showMessageDialog(this, "Successfully Exported! Check folder:\n" + folder);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to export PDF: " + ex.getMessage());
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }
}
