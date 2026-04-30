package com.messutility.db;

import com.messutility.core.DatabaseManager;
import com.messutility.models.expenses.Expense;
import com.messutility.models.expenses.MealExpense;
import com.messutility.models.expenses.UtilityBill;
import com.messutility.models.users.Resident;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpenseDAO {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    public static void addExpense(Expense expense, String type) {
        String query = "INSERT INTO expenses (id, type, amount, description, status, paid_by, due_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setString(1, expense.getId() != null ? expense.getId() : java.util.UUID.randomUUID().toString());
            stmt.setString(2, type); // 'MEAL' or 'UTILITY'
            stmt.setDouble(3, expense.getAmount());
            stmt.setString(4, expense.getDescription());
            stmt.setString(5, expense.isApproved() ? "APPROVED" : "PENDING");
            stmt.setString(6, expense.getPaidBy() != null ? expense.getPaidBy().getId() : null);
            stmt.setString(7, expense.getDueDate() != null ? SDF.format(expense.getDueDate()) : null);
            
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        String query = "SELECT * FROM expenses";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                String id = rs.getString("id");
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                String desc = rs.getString("description");
                String statusStr = rs.getString("status");
                String paidById = rs.getString("paid_by");
                String dueDateStr = rs.getString("due_date");

                Date dueDate = dueDateStr != null ? SDF.parse(dueDateStr) : new Date();
                
                // Construct a placeholder Resident object. 
                // In a real app, we might want to fetch the full Resident from UserDAO.
                Resident paidBy = null;
                if (paidById != null && !paidById.trim().isEmpty()) {
                    paidBy = UserDAO.getResidentById(paidById);
                }

                Expense expense;
                if ("UTILITY".equalsIgnoreCase(type)) {
                    expense = new UtilityBill(id, new Date(), amount, desc, paidBy, dueDate);
                } else {
                    expense = new MealExpense(id, new Date(), amount, desc, paidBy, dueDate);
                }
                
                if ("APPROVED".equalsIgnoreCase(statusStr)) {
                    expense.approve();
                }

                expenses.add(expense);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return expenses;
    }
}
