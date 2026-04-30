package com.messutility.models.expenses;

import com.messutility.models.users.Resident;
import java.util.Date;
import java.util.List;

public abstract class Expense {
    protected String id;
    protected Date date;
    protected double amount;
    protected String description;
    protected ExpenseStatus status;
    protected Resident paidBy; // Resident who paid out of pocket
    protected Date dueDate;    // Added Due Date for Penalty System

    public Expense(String id, Date date, double amount, String description, Resident paidBy, Date dueDate) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.paidBy = paidBy;
        this.dueDate = dueDate;
        this.status = ExpenseStatus.PENDING; // Defaults to PENDING upon submission
    }

    // Should only be called by Manager class
    public void approve() {
        this.status = ExpenseStatus.APPROVED;
    }

    public boolean isApproved() {
        return this.status == ExpenseStatus.APPROVED;
    }

    public String getId() { return id; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public Resident getPaidBy() { return paidBy; }
    public Date getDueDate() { return dueDate; }

    // Abstract method to execute the splitting logic and assign debts
    public abstract void calculateAndAssignSplit(List<Resident> allResidents, double costPerMeal);
}
