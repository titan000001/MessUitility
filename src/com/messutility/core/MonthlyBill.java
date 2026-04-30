package com.messutility.core;

import com.messutility.models.users.Resident;
import java.util.Date;

public class MonthlyBill {
    private Resident resident;
    private double amountDue; // The Net Balance if it's negative (meaning they owe money)
    private Date generatedDate;
    private Date dueDate;
    private boolean isPaid;
    private double lateFee;

    public MonthlyBill(Resident resident, double amountDue, Date generatedDate, Date dueDate) {
        this.resident = resident;
        this.amountDue = amountDue;
        this.generatedDate = generatedDate;
        this.dueDate = dueDate;
        this.isPaid = false;
        this.lateFee = 0.0;
    }

    public void applyLateFee(double feePenalty) {
        if (!isPaid && new Date().after(dueDate)) {
            this.lateFee += feePenalty;
            this.resident.addOwed(feePenalty); // Updates their total owed in the system
            System.out.println("SYSTEM WARNING: Late fee of $" + String.format("%.2f", feePenalty) + " applied to " + resident.getName() + " for missing the due date.");
        }
    }

    public void markAsPaid() {
        this.isPaid = true;
    }

    public Resident getResident() { return resident; }
    public double getAmountDue() { return amountDue + lateFee; }
    public boolean isPaid() { return isPaid; }
}
