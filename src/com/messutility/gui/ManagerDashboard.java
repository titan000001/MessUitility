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
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make the table read-only
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(240, 242, 245));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(220, 225, 230));
        table.setSelectionBackground(new Color(41, 128, 185));
        table.setSelectionForeground(Color.WHITE);
        table.setFocusable(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        Runnable loadData = () -> {
            model.setRowCount(0);
            for (Resident r : UserDAO.getAllResidents()) {
                String role = r.getId().startsWith("G_") ? "GUEST" : "RESIDENT";
                if (r.getId().startsWith("R_admin")) role = "ADMIN RESIDENT";
                model.addRow(new Object[]{r.getId(), r.getName(), role, r.getContact()});
            }
        };
        
        // Initial load
        loadData.run();

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton editBtn = new JButton("Edit Member Info");
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        editBtn.setBackground(new Color(243, 156, 18)); // Orange color for Edit
        editBtn.setForeground(Color.WHITE);
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a member from the table first!");
                return;
            }
            String id = (String) model.getValueAt(row, 0);
            String currentName = (String) model.getValueAt(row, 1);
            String currentContact = (String) model.getValueAt(row, 3);
            
            JPanel editPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            JTextField nameField = new JTextField(currentName);
            JTextField contactField = new JTextField(currentContact);
            editPanel.add(new JLabel("Name:"));
            editPanel.add(nameField);
            editPanel.add(new JLabel("Contact:"));
            editPanel.add(contactField);
            
            int result = JOptionPane.showConfirmDialog(this, editPanel, "Edit Member: " + id, JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                UserDAO.updateUser(id, nameField.getText(), contactField.getText());
                loadData.run();
                JOptionPane.showMessageDialog(this, "Info updated successfully!");
            }
        });

        JButton addGuestBtn = new JButton("Register Guest");
        addGuestBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addGuestBtn.setBackground(new Color(155, 89, 182));
        addGuestBtn.setForeground(Color.WHITE);
        addGuestBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter Guest Name:");
            if (name != null && !name.trim().isEmpty()) {
                String hostId = JOptionPane.showInputDialog(this, "Enter the Host's Resident ID (e.g. R_1a2b3):");
                if (hostId != null && !hostId.trim().isEmpty()) {
                    String id = "G_" + UUID.randomUUID().toString().substring(0, 5);
                    Resident r = new Resident(id, name, "N/A", "guestpass");
                    UserDAO.addUser(r, "GUEST");
                    UserDAO.linkGuestToHost(id, hostId);
                    loadData.run();
                    JOptionPane.showMessageDialog(this, "Guest added and linked to " + hostId + "!");
                }
            }
        });

        JButton linkGuestBtn = new JButton("Link Guest to Host");
        linkGuestBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        linkGuestBtn.setBackground(new Color(52, 152, 219));
        linkGuestBtn.setForeground(Color.WHITE);
        linkGuestBtn.addActionListener(e -> {
            String guestId = JOptionPane.showInputDialog(this, "Enter Guest ID (e.g. G_1a2b3):");
            if (guestId != null && guestId.startsWith("G_")) {
                String hostId = JOptionPane.showInputDialog(this, "Enter the Co-Host's Resident ID:");
                if (hostId != null && hostId.startsWith("R_")) {
                    UserDAO.linkGuestToHost(guestId, hostId);
                    JOptionPane.showMessageDialog(this, hostId + " is now a co-host for " + guestId);
                }
            }
        });

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
                loadData.run();
                JOptionPane.showMessageDialog(this, "Resident added! Default password is 'password123'");
            }
        });

        JButton refreshBtn = new JButton("Refresh List");
        refreshBtn.addActionListener(e -> loadData.run());
        
        btnPanel.add(refreshBtn);
        btnPanel.add(editBtn);
        btnPanel.add(addGuestBtn);
        btnPanel.add(linkGuestBtn);
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
        JList<String> daysList = new JList<>(daysModel);

        Runnable loadDates = () -> {
            int selected = daysList.getSelectedIndex();
            daysModel.clear();
            java.util.Map<String, Integer> mealTotals = com.messutility.db.MealDAO.getDailyMealTotals();
            for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
                String dateStr = yearMonth.atDay(i).toString();
                int total = mealTotals.getOrDefault(dateStr, 0);
                if (total > 0) {
                    daysModel.addElement(dateStr + "   (" + total + " meals) ★");
                } else {
                    daysModel.addElement(dateStr);
                }
            }
            if (selected >= 0 && selected < daysModel.getSize()) {
                daysList.setSelectedIndex(selected);
            } else if (daysModel.getSize() > 0) {
                int dayIndex = today.getDayOfMonth() - 1;
                if (dayIndex >= 0 && dayIndex < daysModel.getSize()) {
                    daysList.setSelectedIndex(dayIndex);
                }
            }
        };

        loadDates.run();

        daysList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        daysList.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane leftScroll = new JScrollPane(daysList);
        leftScroll.setPreferredSize(new Dimension(200, 0));
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
            String selectedDisplay = daysList.getSelectedValue();
            if (selectedDisplay == null) return;
            String selectedDate = selectedDisplay.substring(0, 10);
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
            String selectedDisplay = daysList.getSelectedValue();
            if (selectedDisplay == null) return;
            String selectedDate = selectedDisplay.substring(0, 10);
            
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
            loadDates.run();
            JOptionPane.showMessageDialog(this, "Meals successfully saved for " + selectedDate + "!");
        });
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(saveBtn);
        rightPanel.add(btnPanel, BorderLayout.SOUTH);

        panel.add(rightPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createExpensesTab() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTabbedPane innerTabs = new JTabbedPane();
        innerTabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // --- TAB 1: EXPENSE LOG ---
        JPanel expenseLogTab = new JPanel(new BorderLayout(10, 10));

        class ResidentItem {
            String id;
            String name;
            ResidentItem(String id, String name) { this.id = id; this.name = name; }
            @Override public String toString() { return name; }
        }

        // Form Panel for Adding Expense
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Expense"));
        
        JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"MEAL", "UTILITY"});
        JTextField amountField = new JTextField(5);
        JTextField descField = new JTextField(10);
        
        JComboBox<ResidentItem> residentBox = new JComboBox<>();
        residentBox.addItem(new ResidentItem(null, "None (Manager Paid)"));
        for (Resident r : UserDAO.getAllResidents()) {
            residentBox.addItem(new ResidentItem(r.getId(), r.getName()));
        }

        // Dynamically refresh the dropdown list when clicked
        residentBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                ResidentItem selected = (ResidentItem) residentBox.getSelectedItem();
                residentBox.removeAllItems();
                residentBox.addItem(new ResidentItem(null, "None (Manager Paid)"));
                for (Resident r : UserDAO.getAllResidents()) {
                    ResidentItem item = new ResidentItem(r.getId(), r.getName());
                    residentBox.addItem(item);
                    if (selected != null && selected.id != null && selected.id.equals(r.getId())) {
                        residentBox.setSelectedItem(item);
                    }
                }
                if (selected != null && selected.id == null) {
                    residentBox.setSelectedIndex(0);
                }
            }
            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });

        fieldsPanel.add(new JLabel("Type:"));
        fieldsPanel.add(typeBox);
        fieldsPanel.add(new JLabel("Amount:"));
        fieldsPanel.add(amountField);
        fieldsPanel.add(new JLabel("Desc:"));
        fieldsPanel.add(descField);
        fieldsPanel.add(new JLabel("Paid By:"));
        fieldsPanel.add(residentBox);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton addBtn = new JButton("Add Expense");
        addBtn.setBackground(new Color(230, 126, 34));
        addBtn.setForeground(Color.WHITE);
        actionPanel.add(addBtn);

        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(actionPanel, BorderLayout.SOUTH);
        
        expenseLogTab.add(formPanel, BorderLayout.NORTH);

        // Expense Table
        String[] columns = {"ID", "Date", "Type", "Amount", "Description", "Paid By"};
        DefaultTableModel expenseModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable expenseTable = new JTable(expenseModel);
        expenseTable.setRowHeight(30);
        expenseTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        expenseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Hide Expense ID column
        expenseTable.getColumnModel().getColumn(0).setMinWidth(0);
        expenseTable.getColumnModel().getColumn(0).setMaxWidth(0);
        expenseTable.getColumnModel().getColumn(0).setWidth(0);
        
        JScrollPane expenseScroll = new JScrollPane(expenseTable);
        expenseScroll.setBorder(BorderFactory.createTitledBorder("All Logged Expenses"));
        expenseLogTab.add(expenseScroll, BorderLayout.CENTER);
        
        Runnable loadExpenses = () -> {
            expenseModel.setRowCount(0);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            for (com.messutility.models.expenses.Expense exp : com.messutility.db.ExpenseDAO.getAllExpenses()) {
                String paidByName = exp.getPaidBy() != null ? exp.getPaidBy().getName() : "Manager";
                String typeStr = (exp instanceof com.messutility.models.expenses.UtilityBill) ? "UTILITY" : "MEAL";
                expenseModel.addRow(new Object[]{
                    exp.getId(), 
                    exp.getDueDate() != null ? sdf.format(exp.getDueDate()) : "N/A", 
                    typeStr, 
                    "$" + String.format("%.2f", exp.getAmount()), 
                    exp.getDescription(), 
                    paidByName
                });
            }
        };
        loadExpenses.run();

        addBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String desc = descField.getText();
                String type = (String) typeBox.getSelectedItem();
                
                Resident paidBy = null;
                ResidentItem selectedItem = (ResidentItem) residentBox.getSelectedItem();
                if (selectedItem != null && selectedItem.id != null) {
                    paidBy = UserDAO.getResidentById(selectedItem.id);
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
                
                loadExpenses.run(); // Refresh list
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Amount must be a number.");
            }
        });
        
        innerTabs.addTab("Expense Log", expenseLogTab);

        // --- TAB 2: MONTHLY SETTLEMENT ---
        JPanel settlementTab = new JPanel(new BorderLayout(10, 10));
        
        JTextArea billsArea = new JTextArea();
        billsArea.setEditable(false);
        billsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(billsArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Generated Monthly Bills"));

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

        settlementTab.add(scrollPane, BorderLayout.CENTER);
        settlementTab.add(bottomPanel, BorderLayout.SOUTH);

        innerTabs.addTab("Monthly Settlement", settlementTab);
        
        mainPanel.add(innerTabs, BorderLayout.CENTER);

        return mainPanel;
    }
}
